package com.plantcloud.control.controller;

import com.plantcloud.common.result.Result;
import com.plantcloud.control.dto.DeviceControlRequest;
import com.plantcloud.control.service.DeviceCommandService;
import com.plantcloud.control.vo.ControlCommandVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/control")
@RequiredArgsConstructor
public class ControlController {

    private final DeviceCommandService deviceCommandService;

    @PostMapping("/light")
    public Result<ControlCommandVO> controlLight(@Valid @RequestBody DeviceControlRequest request) {
        log.info("[CTRL] request received target=light plantId={} deviceId={} commandValue={}",
                request.getPlantId(), request.getDeviceId(), request.getCommandValue());
        return Result.ok(deviceCommandService.controlLight(request));
    }

    @PostMapping("/fan")
    public Result<ControlCommandVO> controlFan(@Valid @RequestBody DeviceControlRequest request) {
        log.info("[CTRL] request received target=fan plantId={} deviceId={} commandValue={}",
                request.getPlantId(), request.getDeviceId(), request.getCommandValue());
        return Result.ok(deviceCommandService.controlFan(request));
    }
}
