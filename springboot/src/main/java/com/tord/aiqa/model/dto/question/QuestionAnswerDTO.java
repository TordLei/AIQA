package com.tord.aiqa.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionAnswerDTO {
    /**
     * 题目
     */
    private String title;
    /**
     * 答案
     */
    private String userAnswer;
}
