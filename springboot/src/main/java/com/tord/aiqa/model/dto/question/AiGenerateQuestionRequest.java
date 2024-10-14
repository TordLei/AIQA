package com.tord.aiqa.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AiGenerateQuestionRequest implements Serializable {
    /**
     * 应用Id
     */
    private Long appId;
    /**
     * 题目数
     */
    int questionNumber = 10;
    /**
     * 选项数
     */
    int optionNumber = 2;
}
