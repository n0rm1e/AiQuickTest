package com.normie.aiquicktest.model.dto.userAnswer;

import lombok.Data;

/**
 * 用户答案
 * 用于AI根据答案生成测评结果
 */
@Data
public class QuestionAnswerDTO {
    private String title;
    private String answer;

    public QuestionAnswerDTO(String title, String result) {
        this.title = title;
        this.answer = result;
    }
}
