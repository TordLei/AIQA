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
import java.util.Optional;

/**
 * 自定义测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
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
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        //2、计算用户总得分
        int totalScore = 0;
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
                        int score = Optional.of(option.getScore()).orElse(0);
                        totalScore += score;
                    }
                }
            }
        }
        ScoringResult maxScoringResult = scoringResultList.get(0);
        //3、遍历得分结果，拿到第一个用户分数大于得分范围的结果，作为最终结果。
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
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
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
