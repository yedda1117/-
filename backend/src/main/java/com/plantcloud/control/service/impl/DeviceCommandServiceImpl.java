package com.plantcloud.control.service.impl;

import com.plantcloud.control.dto.DeviceControlRequest;
import com.plantcloud.control.service.DeviceCommandService;
import com.plantcloud.control.vo.ControlCommandVO;
import org.springframework.stereotype.Service;

@Service
public class DeviceCommandServiceImpl implements DeviceCommandService {

    @Override
    public ControlCommandVO controlLight(DeviceControlRequest request) {
        return buildResponse("LIGHT_SWITCH", request.getCommandValue());
    }

    @Override
    public ControlCommandVO controlFan(DeviceControlRequest request) {
        return buildResponse("FAN_SWITCH", request.getCommandValue());
    }

    private ControlCommandVO buildResponse(String commandName, String commandValue) {
        return ControlCommandVO.builder()
                .commandLogId(0L)
                .commandName(commandName)
                .commandValue(commandValue)
                .executeStatus("PENDING")
                .message("控制指令已受理，待设备回执")
                .build();
    }
}
