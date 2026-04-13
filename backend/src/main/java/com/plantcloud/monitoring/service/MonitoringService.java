package com.plantcloud.monitoring.service;

import com.plantcloud.monitoring.vo.CurrentEnvironmentVO;
import com.plantcloud.monitoring.vo.DeviceStatusOverviewVO;

public interface MonitoringService {

    CurrentEnvironmentVO getCurrentEnvironment(Long plantId);

    DeviceStatusOverviewVO getDeviceStatus(Long plantId);
}
