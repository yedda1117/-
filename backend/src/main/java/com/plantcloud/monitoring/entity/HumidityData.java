package com.plantcloud.monitoring.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("humidity_data")
@EqualsAndHashCode(callSuper = true)
public class HumidityData extends BaseEntity {

    private Long plantId;
    private Long deviceId;
    private BigDecimal humidity;
    private String rawPayload;
    private LocalDateTime collectedAt;
}
