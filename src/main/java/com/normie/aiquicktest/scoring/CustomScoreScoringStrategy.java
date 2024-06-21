package com.normie.aiquicktest.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.normie.aiquicktest.common.Pair;
import com.normie.aiquicktest.model.dto.question.QuestionContentDTO;
import com.normie.aiquicktest.model.entity.App;
import com.normie.aiquicktest.model.entity.Question;
import com.normie.aiquicktest.model.entity.ScoringResult;
import com.normie.aiquicktest.model.entity.UserAnswer;
import com.normie.aiquicktest.model.vo.QuestionVO;
import com.normie.aiquicktest.service.QuestionService;
import com.normie.aiquicktest.service.ScoringResultService;
import scala.Int;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 自定义得分类应用评分策略
 */
@ScoringStrategyConfig(appType = 0, scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {

    @Resource
    private QuestionService questionService;

    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 根据 id 查询到题目和题目结果信息（按分数降序排序）
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        List<ScoringResult> scoringResultList = scoringResultService.list(
                Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId)
                        .orderByDesc(ScoringResult::getResultScoreRange)
        );

        // 统计用户的总得分
        int totalScore = 0;
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();

        // 获取答案和得分列表
        ArrayList<Pair<String, Integer>> answerList = new ArrayList<>();
        for (QuestionContentDTO questionContentDTO : questionContent) {
            List<QuestionContentDTO.Option> options = questionContentDTO.getOptions();
            for (QuestionContentDTO.Option option : options) {
                if (option.getScore() != 0) {
                    answerList.add(new Pair<>(option.getKey(), option.getScore()));
                }
            }
        }
        // 判题并加分
        for (int i = 0; i < choices.size(); i++) {
            if (answerList.get(i).getLeft().equals(choices.get(i))) {
                totalScore += answerList.get(i).getRight();
            }
        }

        // 遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if (totalScore >= scoringResult.getResultScoreRange()) {
                maxScoringResult = scoringResult;
                break;
            }
        }

        // 4. 构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
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
