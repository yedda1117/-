package com.plantcloud.alert.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("alert_logs")
@EqualsAndHashCode(callSuper = true)
public class AlertLog extends BaseEntity {

    private Long plantId;
    private Long deviceId;
    private String alertType;
    private String severity;
    private String title;
    private String content;
    private String metricName;
    private BigDecimal metricValue;
    private BigDecimal thresholdValue;
    private String status;
    private Long resolvedBy;
    private LocalDateTime resolvedAt;
    private String extraData;
    private LocalDateTime createdAt;
}
