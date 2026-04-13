package com.plantcloud.visualization.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StrategyExecutionLogVO {

    private Long strategyId;
    private String strategyName;
    private String triggerSource;
    private String executionResult;
    private String resultMessage;
    private String executedAt;
}
