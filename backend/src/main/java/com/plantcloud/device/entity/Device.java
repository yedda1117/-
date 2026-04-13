package com.plantcloud.device.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("devices")
@EqualsAndHashCode(callSuper = true)
public class Device extends BaseEntity {

    private Long plantId;
    private String deviceCode;
    private String deviceName;
    private String deviceType;
    private String mqttTopicUp;
    private String mqttTopicDown;
    private String onlineStatus;
    private String currentStatus;
    private String firmwareVersion;
    private LocalDateTime lastSeenAt;
}
