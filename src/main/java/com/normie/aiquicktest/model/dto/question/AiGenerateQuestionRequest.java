package com.normie.aiquicktest.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * AI生成题目请求
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 生成题目数量
     */
    int questionNumber = 10;

    /**
     * 每道题的选项数量
     */
    int optionNumber = 2;

}
