package com.plantcloud.strategy.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("strategy_execution_logs")
@EqualsAndHashCode(callSuper = true)
public class StrategyExecutionLog extends BaseEntity {

    private Long strategyId;
    private Long plantId;
    private String triggerSource;
    private BigDecimal triggerMetricValue;
    private String triggerPayload;
    private String executionResult;
    private String resultMessage;
    private Long commandLogId;
    private LocalDateTime executedAt;
}
