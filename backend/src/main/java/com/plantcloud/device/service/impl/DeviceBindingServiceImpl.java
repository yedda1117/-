package com.plantcloud.device.service.impl;

import com.plantcloud.common.enums.ResultCode;
import com.plantcloud.device.dto.DeviceBindPlantRequest;
import com.plantcloud.device.mapper.DeviceMapper;
import com.plantcloud.device.service.DeviceBindingService;
import com.plantcloud.device.vo.DeviceBindResponseVO;
import com.plantcloud.plant.entity.Plant;
import com.plantcloud.plant.mapper.PlantMapper;
import com.plantcloud.system.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceBindingServiceImpl implements DeviceBindingService {

    private static final String ACTIVE_PLANT_STATUS = "ACTIVE";
    private static final Long BOUND_DEVICE_SET_ID = 1L;
    private static final String BOUND_DEVICE_SET_CODE = "1,2,3,4,5,6,7";

    private final DeviceMapper deviceMapper;
    private final PlantMapper plantMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DeviceBindResponseVO bindPlant(DeviceBindPlantRequest request) {
        Plant plant = requireActivePlant(request.getPlantId());
        deviceMapper.bindPlantToDeviceSet(plant.getId());
        return buildResponse(plant);
    }

    private Plant requireActivePlant(Long plantId) {
        Plant plant = plantMapper.selectById(plantId);
        if (plant == null) {
            throw new BizException(ResultCode.NOT_FOUND.getCode(), "Plant not found");
        }
        if (!ACTIVE_PLANT_STATUS.equalsIgnoreCase(plant.getStatus())) {
            throw new BizException(ResultCode.BAD_REQUEST.getCode(), "Plant status does not allow binding");
        }
        return plant;
    }

    private DeviceBindResponseVO buildResponse(Plant plant) {
        return DeviceBindResponseVO.builder()
                .deviceId(BOUND_DEVICE_SET_ID)
                .deviceCode(BOUND_DEVICE_SET_CODE)
                .plantId(plant.getId())
                .plantName(plant.getPlantName())
                .build();
    }
}
