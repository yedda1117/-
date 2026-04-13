package com.plantcloud.monitoring.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("light_data")
@EqualsAndHashCode(callSuper = true)
public class LightData extends BaseEntity {

    private Long plantId;
    private Long deviceId;
    private BigDecimal lightLux;
    private String rawPayload;
    private LocalDateTime collectedAt;
}
