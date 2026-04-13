package com.plantcloud.monitoring.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CurrentEnvironmentVO {

    private Long plantId;
    private BigDecimal temperature;
    private BigDecimal humidity;
    private BigDecimal lightLux;
    private String temperatureStatus;
    private String humidityStatus;
    private String lightStatus;
    private LocalDateTime collectedAt;
}
