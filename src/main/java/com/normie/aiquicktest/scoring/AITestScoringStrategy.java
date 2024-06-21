package com.normie.aiquicktest.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.normie.aiquicktest.manager.AiManager;
import com.normie.aiquicktest.model.dto.question.QuestionContentDTO;
import com.normie.aiquicktest.model.entity.App;
import com.normie.aiquicktest.model.entity.Question;
import com.normie.aiquicktest.model.entity.ScoringResult;
import com.normie.aiquicktest.model.entity.UserAnswer;
import com.normie.aiquicktest.model.vo.QuestionVO;
import com.normie.aiquicktest.service.QuestionService;
import com.normie.aiquicktest.service.ScoringResultService;
import springfox.documentation.spring.web.json.Json;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AITestScoringStrategy implements ScoringStrategy {
    @Resource
    ScoringResultService scoringResultService;

    @Resource
    QuestionService questionService;

    @Resource
    AiManager aiManager;

    private static final String AI_GENERATE_SCORING_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息:\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表:格式为[{\"title\":\"题目\",\"answer\":“用户回答\"}]\n" +
            "```\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价:\n" +
            "1.要求:需要给出一个明确的评价结果，包括评价名称(尽量简短)和评价描述(尽量详细，大于200字)\n" +
            "2.严格按照下面的json格式输出评价名称和评价描述\n" +
            "{\"resultName\":\"评价名称\",\"resultDesc\":\"评价描述\"}\n" +
            "3.返回格式必须为JSON对象\n" +
            "4.评价时主语用”你“";

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        System.out.println("AI测评类应用评分开始");
        Long appId = app.getId();
        // 根据 id 查询到题目和题目结果信息
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        // 生成用户信息
        String scoringGenerateUserMessage = scoringResultService.getScoringGenerateUserMessage(choices, questionContent, app);
        // AI生成测评结果
        String result = aiManager.doSyncStableRequest(AI_GENERATE_SCORING_SYSTEM_MESSAGE, scoringGenerateUserMessage);
        // 处理结果字符串
        int start = result.indexOf("{");
        int end = result.lastIndexOf("}");
        String resultStr = result.substring(start, end + 1);
        // 此时得到的userAnswer只有评价名称和评价描述
        UserAnswer userAnswer = JSONUtil.toBean(resultStr, UserAnswer.class);
        // 填充userAnswer的其他信息
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        return userAnswer;
    }
}
