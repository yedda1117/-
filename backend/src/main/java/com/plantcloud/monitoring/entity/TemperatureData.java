package com.plantcloud.monitoring.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("temperature_data")
@EqualsAndHashCode(callSuper = true)
public class TemperatureData extends BaseEntity {

    private Long plantId;
    private Long deviceId;
    private BigDecimal temperature;
    private String rawPayload;
    private LocalDateTime collectedAt;
}
