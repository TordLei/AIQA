package com.tord.aiqa;

import com.tord.aiqa.common.ErrorCode;
import com.tord.aiqa.controller.QuestionController;
import com.tord.aiqa.exception.ThrowUtils;
import com.tord.aiqa.model.dto.question.AiGenerateQuestionRequest;
import com.tord.aiqa.model.dto.question.QuestionAddRequest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
    @Resource
    private RedissonClient redissonClient;
    
    @Resource
    private QuestionController questionController;
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";

    @Test
    void Test01() throws InterruptedException {
        AiGenerateQuestionRequest aiGenerateQuestionRequest = new AiGenerateQuestionRequest();
        aiGenerateQuestionRequest.setAppId(3L);
        aiGenerateQuestionRequest.setQuestionNumber(10);
        aiGenerateQuestionRequest.setOptionNumber(2);
        questionController.aiGenerateQuestionSseTest(aiGenerateQuestionRequest,false);
        questionController.aiGenerateQuestionSseTest(aiGenerateQuestionRequest,false);
        questionController.aiGenerateQuestionSseTest(aiGenerateQuestionRequest,true);
        Thread.sleep(30000L);
    }


}
