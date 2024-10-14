package com.tord.aiqa;

import com.tord.aiqa.common.ErrorCode;
import com.tord.aiqa.exception.ThrowUtils;
import com.tord.aiqa.manager.AiManager;
import com.tord.aiqa.model.entity.App;
import com.tord.aiqa.model.enums.AppTypeEnum;
import com.tord.aiqa.service.AppService;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class ZhiPuAiTest {
    @Resource
    private ClientV4 clientV4;

    @Resource
    private AppService appService;

    @Resource
    private AiManager aiManager;

    @Test
    public void test() {
//        ClientV4 client = new ClientV4.Builder("b51222fe51eeef1bc107e90817768804.rKu1gMdGBVFHGBNr").build();
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage1 = new ChatMessage(ChatMessageRole.SYSTEM.value(), GENERATE_QUESTION_SYSTEM_MESSAGE);
        messages.add(chatMessage1);
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "MBTI测试");
        messages.add(chatMessage);
//            String requestId = String.format(requestIdTemplate, System.currentTimeMillis());

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model("glm-4-plus")
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        System.out.println("model output:" + invokeModelApiResp.getData().getChoices().get(0));
    }

    @Test
    public void test2() {
        System.out.println(getGenerateQuestionUserMessage(appService.getById(1), 5, 2));
    }

    private String getGenerateQuestionUserMessage(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }

    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的出题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "应用类别，\n" +
            "要生成的题目数，\n" +
            "每个题目的选项数\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来出题：\n" +
            "1. 要求：题目和选项尽可能地短，题目不要包含序号，每题的选项数以我提供的为主，题目不能重复\n" +
            "2. 严格按照下面的 json 格式输出题目和选项\n" +
            "```\n" +
            "[{\"options\":[{\"value\":\"选项内容\",\"key\":\"A\"},{\"value\":\"\",\"key\":\"B\"}],\"title\":\"题目标题\"}]\n" +
            "```\n" +
            "title 是题目，options 是选项，每个选项的 key 按照英文字母序（比如 A、B、C、D）以此类推，value 是选项内容\n" +
            "3. 检查题目是否包含序号，若包含序号则去除序号\n" +
            "4. 返回的题目列表格式必须为 JSON 数组";

    private String getGenerateQuestionUserMessage1(App app, int questionNumber, int optionNumber) {
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(AppTypeEnum.getEnumByValue(app.getAppType()).getText() + "类").append("\n");
        userMessage.append(questionNumber).append("\n");
        userMessage.append(optionNumber);
        return userMessage.toString();
    }

    @Test
    public void aiGenerateQuestion() {
        Long appId = 1842142088750313474L;
        int questionNumber = 10;
        int optionNumber = 2;
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        //构造prompt
        String userMessage = getGenerateQuestionUserMessage1(app, questionNumber, optionNumber);
        //AI生成
        String aiResult = aiManager.doSyncUnstableRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage);
        int start = aiResult.indexOf("[");
        int end = aiResult.lastIndexOf("]");
        String aiAnswerJson = aiResult.substring(start, end + 1);
        System.out.println(aiAnswerJson);
//        List<QuestionContentDTO> aiAnswer = JSONUtil.toList(aiAnswerJson, QuestionContentDTO.class);
    }

}
