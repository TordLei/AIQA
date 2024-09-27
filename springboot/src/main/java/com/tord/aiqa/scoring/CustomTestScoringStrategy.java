package com.tord.aiqa.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.tord.aiqa.model.dto.question.QuestionContentDTO;
import com.tord.aiqa.model.entity.App;
import com.tord.aiqa.model.entity.Question;
import com.tord.aiqa.model.entity.ScoringResult;
import com.tord.aiqa.model.entity.UserAnswer;
import com.tord.aiqa.model.vo.QuestionVO;
import com.tord.aiqa.service.QuestionService;
import com.tord.aiqa.service.ScoringResultService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ScoringStrategyConfig(appType = 1,scoringStrategy = 0)
public class CustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        //1、根据id查询到题目和题目结果信息
        Long id = app.getId();
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId,id)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, id)
        );

        //2、统计用户每个选择对应的属性个数，如 I = 10 ， E =
        Map<String, Integer> optionCount = new HashMap<>();
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        //遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContent) {
            //遍历答案列表
            for (String choice : choices) {
                //遍历题目中的选项
                for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                    //如果答案和选项的key匹配，key在map中加1
                    if (option.getKey().equals(choice)){
                        String result = option.getResult();
                        if (!optionCount.containsKey(result)){
                            optionCount.put(result,0);
                        }
                        optionCount.put(result,optionCount.get(result)+1);
                    }
                }
            }
        }

        //3、遍历每种评分结果，计算哪个结果的得分更高
        int maxScore = 0;
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            List<String> resultProp = JSONUtil.toList(scoringResult.getResultProp(), String.class);
            int score = resultProp.stream().mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();
            if (score>maxScore){
                maxScoringResult = scoringResult;
            }
        }

        //4、构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(app.getId());
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        return userAnswer;
    }
}
