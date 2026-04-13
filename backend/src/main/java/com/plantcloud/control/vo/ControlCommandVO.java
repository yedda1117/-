package com.plantcloud.control.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ControlCommandVO {

    private Long commandLogId;
    private String commandName;
    private String commandValue;
    private String executeStatus;
    private String message;
}
