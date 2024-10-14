package com.tord.aiqa.scoring;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.tord.aiqa.config.RedissonConfig;
import com.tord.aiqa.manager.AiManager;
import com.tord.aiqa.model.dto.question.QuestionAnswerDTO;
import com.tord.aiqa.model.dto.question.QuestionContentDTO;
import com.tord.aiqa.model.entity.App;
import com.tord.aiqa.model.entity.Question;
import com.tord.aiqa.model.entity.ScoringResult;
import com.tord.aiqa.model.entity.UserAnswer;
import com.tord.aiqa.model.enums.AppTypeEnum;
import com.tord.aiqa.model.vo.QuestionVO;
import com.tord.aiqa.service.QuestionService;
import com.tord.aiqa.service.ScoringResultService;
import org.checkerframework.checker.units.qual.A;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Ai测评类评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;
    @Resource
    private RedissonClient redissonClient;

    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存5分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();


    private static final String AI_TEST_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象";


    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //1、根据id查询到题目和题目结果信息
        Long id = app.getId();
        //查询缓存中是否有结果
        String jsonChoices = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(id, jsonChoices);
        String answer = answerCacheMap.getIfPresent(cacheKey);
        if (StrUtil.isNotBlank(answer)) {
            UserAnswer userAnswer = JSONUtil.toBean(answer, UserAnswer.class);
            userAnswer.setAppId(app.getId());
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(jsonChoices);
            return userAnswer;
        }

        //没有缓存。锁住，正常评分再设置缓存
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK);
        try {
            //竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            //没抢到锁，返回
            if (!res){
                System.out.println(Thread.currentThread().getName()+"没拿到锁");
                return null;
            }else {
                System.out.println(Thread.currentThread().getName()+"拿到锁了");
            }
            //抢到锁了，继续执行
            Question question = questionService.getOne(
                    Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, id)
            );
            QuestionVO questionVO = QuestionVO.objToVo(question);
            List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

            //2、构造用户prompt
            String aiTestScoringUserMessage = getAiTestScoringUserMessage(app, questionContent, choices);
            //3、调用大模型接口,生成用户评价
            String result = aiManager.doSyncStableRequest(AI_TEST_SCORING_SYSTEM_MESSAGE, aiTestScoringUserMessage);
            int start = result.indexOf('{');
            int end = result.lastIndexOf('}');
            String userAnswerResult = result.substring(start, end + 1);

            //AI生成结果以后，存入缓存
            answerCacheMap.put(cacheKey, userAnswerResult);

            //4、将AI生成的结果放到userAnswer里面
            UserAnswer userAnswer = JSONUtil.toBean(userAnswerResult, UserAnswer.class);
            userAnswer.setAppId(app.getId());
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            return userAnswer;
        } finally {
            if (lock != null && lock.isLocked()) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    private String getAiTestScoringUserMessage(App app, List<QuestionContentDTO> questionContentDTOList, List<String> choices) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTOList.get(i).getTitle());
            questionAnswerDTO.setUserAnswer(choices.get(i));
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }

    private String buildCacheKey(Long appId, String choices) {
        return DigestUtil.md5Hex(appId + ':' + choices);
    }

}
