package com.normie.aiquicktest.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.normie.aiquicktest.model.dto.question.QuestionQueryRequest;
import com.normie.aiquicktest.model.entity.App;
import com.normie.aiquicktest.model.entity.Question;
import com.normie.aiquicktest.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 题目服务

 */
public interface QuestionService extends IService<Question> {

    /**
     * 校验数据
     *
     * @param question
     * @param add 对创建的数据进行校验
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);
    
    /**
     * 获取题目封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);

    /**
     * 获取问题生成用户信息
     * @param app
     * @param questionNumber
     * @param optionNumber
     * @return
     */
    String getQuestionGenerateUserMessage(App app, int questionNumber, int optionNumber);
}
