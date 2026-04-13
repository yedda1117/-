package com.plantcloud.monitoring.controller;

import com.plantcloud.common.result.Result;
import com.plantcloud.monitoring.service.MonitoringService;
import com.plantcloud.monitoring.vo.CurrentEnvironmentVO;
import com.plantcloud.monitoring.vo.DeviceStatusOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitoring")
@RequiredArgsConstructor
public class MonitoringController {

    private final MonitoringService monitoringService;

    @GetMapping("/environment/current")
    public Result<CurrentEnvironmentVO> getCurrentEnvironment(@RequestParam Long plantId) {
        return Result.ok(monitoringService.getCurrentEnvironment(plantId));
    }

    @GetMapping("/devices/status")
    public Result<DeviceStatusOverviewVO> getDeviceStatus(@RequestParam Long plantId) {
        return Result.ok(monitoringService.getDeviceStatus(plantId));
    }
}
