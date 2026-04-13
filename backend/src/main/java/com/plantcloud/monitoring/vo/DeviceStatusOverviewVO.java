package com.plantcloud.monitoring.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DeviceStatusOverviewVO {

    private Long plantId;
    private List<DeviceStatusVO> devices;

    @Data
    @Builder
    public static class DeviceStatusVO {
        private Long deviceId;
        private String deviceCode;
        private String deviceName;
        private String deviceType;
        private String onlineStatus;
        private String currentStatus;
    }
}
