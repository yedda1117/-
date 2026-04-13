package com.plantcloud.control.service;

import com.plantcloud.control.dto.DeviceControlRequest;
import com.plantcloud.control.vo.ControlCommandVO;

public interface DeviceCommandService {

    ControlCommandVO controlLight(DeviceControlRequest request);

    ControlCommandVO controlFan(DeviceControlRequest request);
}
