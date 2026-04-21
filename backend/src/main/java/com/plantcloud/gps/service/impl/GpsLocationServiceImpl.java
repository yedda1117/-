package com.plantcloud.gps.service.impl;

import com.plantcloud.common.enums.ResultCode;
import com.plantcloud.gps.entity.GpsLocationLog;
import com.plantcloud.gps.mapper.GpsLocationLogMapper;
import com.plantcloud.gps.service.GpsLocationService;
import com.plantcloud.plant.entity.Plant;
import com.plantcloud.plant.mapper.PlantMapper;
import com.plantcloud.system.exception.BizException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsLocationServiceImpl implements GpsLocationService {

    private final GpsLocationLogMapper gpsLocationLogMapper;
    private final PlantMapper plantMapper;

    @Override
    public List<GpsLocationLog> listByPlantId(Long plantId) {
        Plant plant = plantMapper.selectById(plantId);
        if (plant == null) {
            throw new BizException(ResultCode.NOT_FOUND.getCode(), "Plant not found, plantId=" + plantId);
        }
        return gpsLocationLogMapper.selectByPlantId(plantId);
    }
}
