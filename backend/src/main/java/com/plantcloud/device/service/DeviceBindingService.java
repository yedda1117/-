package com.plantcloud.device.service;

import com.plantcloud.device.dto.DeviceBindPlantRequest;
import com.plantcloud.device.vo.DeviceBindResponseVO;

public interface DeviceBindingService {

    DeviceBindResponseVO bindPlant(DeviceBindPlantRequest request);
}
