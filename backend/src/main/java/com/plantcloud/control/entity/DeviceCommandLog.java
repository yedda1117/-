package com.plantcloud.control.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("device_command_logs")
@EqualsAndHashCode(callSuper = true)
public class DeviceCommandLog extends BaseEntity {

    private Long plantId;
    private Long deviceId;
    private Long operatorUserId;
    private String sourceType;
    private String commandName;
    private String commandValue;
    private String requestPayload;
    private String responsePayload;
    private String executeStatus;
    private String errorMessage;
    private LocalDateTime executedAt;
    private LocalDateTime createdAt;
}
