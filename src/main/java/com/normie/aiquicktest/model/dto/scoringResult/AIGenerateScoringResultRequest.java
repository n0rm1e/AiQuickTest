package com.normie.aiquicktest.model.dto.scoringResult;

import com.normie.aiquicktest.model.entity.UserAnswer;
import lombok.Data;

import java.io.Serializable;

@Data
public class AIGenerateScoringResultRequest implements Serializable {
    /**
     * 用户回答
     */
    private UserAnswer userAnswer;

}
