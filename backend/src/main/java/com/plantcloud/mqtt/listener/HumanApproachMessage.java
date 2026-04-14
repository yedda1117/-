package com.plantcloud.mqtt.listener;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class HumanApproachMessage {

    @JsonProperty("event_type")
    private String eventType;

    private Integer status;

    private Long timestamp;
}
