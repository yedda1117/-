package com.plantcloud.alert.controller;

import com.plantcloud.alert.service.AlertService;
import com.plantcloud.alert.vo.AlertLogVO;
import com.plantcloud.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping("/current")
    public Result<List<AlertLogVO>> getCurrentAlerts(@RequestParam Long plantId) {
        return Result.ok(alertService.getCurrentAlerts(plantId));
    }

    @GetMapping("/logs")
    public Result<List<AlertLogVO>> getAlertLogs(@RequestParam Long plantId) {
        return Result.ok(alertService.getAlertLogs(plantId));
    }

    @PostMapping("/{alertId}/acknowledge")
    public Result<Void> acknowledge(@PathVariable Long alertId, @RequestParam Long userId) {
        alertService.acknowledge(alertId, userId);
        return Result.ok(null);
    }
}
