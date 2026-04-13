package com.plantcloud.monitoring.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plantcloud.device.entity.Device;
import com.plantcloud.device.mapper.DeviceMapper;
import com.plantcloud.monitoring.entity.HumidityData;
import com.plantcloud.monitoring.entity.LightData;
import com.plantcloud.monitoring.entity.TemperatureData;
import com.plantcloud.monitoring.mapper.HumidityDataMapper;
import com.plantcloud.monitoring.mapper.LightDataMapper;
import com.plantcloud.monitoring.mapper.TemperatureDataMapper;
import com.plantcloud.monitoring.service.MonitoringService;
import com.plantcloud.monitoring.vo.CurrentEnvironmentVO;
import com.plantcloud.monitoring.vo.DeviceStatusOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MonitoringServiceImpl implements MonitoringService {

    private final TemperatureDataMapper temperatureDataMapper;
    private final HumidityDataMapper humidityDataMapper;
    private final LightDataMapper lightDataMapper;
    private final DeviceMapper deviceMapper;

    @Override
    public CurrentEnvironmentVO getCurrentEnvironment(Long plantId) {
        TemperatureData temperatureData = temperatureDataMapper.selectOne(
                new LambdaQueryWrapper<TemperatureData>()
                        .eq(TemperatureData::getPlantId, plantId)
                        .orderByDesc(TemperatureData::getCollectedAt)
                        .last("limit 1")
        );
        HumidityData humidityData = humidityDataMapper.selectOne(
                new LambdaQueryWrapper<HumidityData>()
                        .eq(HumidityData::getPlantId, plantId)
                        .orderByDesc(HumidityData::getCollectedAt)
                        .last("limit 1")
        );
        LightData lightData = lightDataMapper.selectOne(
                new LambdaQueryWrapper<LightData>()
                        .eq(LightData::getPlantId, plantId)
                        .orderByDesc(LightData::getCollectedAt)
                        .last("limit 1")
        );

        BigDecimal temperature = temperatureData != null ? temperatureData.getTemperature() : null;
        BigDecimal humidity = humidityData != null ? humidityData.getHumidity() : null;
        BigDecimal lightLux = lightData != null ? lightData.getLightLux() : null;

        return CurrentEnvironmentVO.builder()
                .plantId(plantId)
                .temperature(temperature)
                .humidity(humidity)
                .lightLux(lightLux)
                .temperatureStatus(resolveTemperatureStatus(temperature))
                .humidityStatus(resolveHumidityStatus(humidity))
                .lightStatus(resolveLightStatus(lightLux))
                .collectedAt(resolveLatestTime(temperatureData, humidityData, lightData))
                .build();
    }

    @Override
    public DeviceStatusOverviewVO getDeviceStatus(Long plantId) {
        List<DeviceStatusOverviewVO.DeviceStatusVO> devices = deviceMapper.selectList(
                        new LambdaQueryWrapper<Device>()
                                .eq(Device::getPlantId, plantId)
                                .orderByAsc(Device::getId)
                ).stream()
                .map(device -> DeviceStatusOverviewVO.DeviceStatusVO.builder()
                        .deviceId(device.getId())
                        .deviceCode(device.getDeviceCode())
                        .deviceName(device.getDeviceName())
                        .deviceType(device.getDeviceType())
                        .onlineStatus(device.getOnlineStatus())
                        .currentStatus(device.getCurrentStatus())
                        .build())
                .toList();

        return DeviceStatusOverviewVO.builder()
                .plantId(plantId)
                .devices(devices)
                .build();
    }

    private String resolveTemperatureStatus(BigDecimal temperature) {
        if (temperature == null) {
            return "UNKNOWN";
        }
        if (temperature.compareTo(BigDecimal.valueOf(18)) < 0) {
            return "LOW";
        }
        if (temperature.compareTo(BigDecimal.valueOf(30)) > 0) {
            return "HIGH";
        }
        return "NORMAL";
    }

    private String resolveHumidityStatus(BigDecimal humidity) {
        if (humidity == null) {
            return "UNKNOWN";
        }
        if (humidity.compareTo(BigDecimal.valueOf(40)) < 0) {
            return "LOW";
        }
        if (humidity.compareTo(BigDecimal.valueOf(80)) > 0) {
            return "HIGH";
        }
        return "NORMAL";
    }

    private String resolveLightStatus(BigDecimal lightLux) {
        if (lightLux == null) {
            return "UNKNOWN";
        }
        if (lightLux.compareTo(BigDecimal.valueOf(300)) < 0) {
            return "LOW";
        }
        if (lightLux.compareTo(BigDecimal.valueOf(30000)) > 0) {
            return "HIGH";
        }
        return "NORMAL";
    }

    private LocalDateTime resolveLatestTime(TemperatureData temperatureData,
                                            HumidityData humidityData,
                                            LightData lightData) {
        LocalDateTime latest = null;
        if (temperatureData != null && temperatureData.getCollectedAt() != null) {
            latest = temperatureData.getCollectedAt();
        }
        if (humidityData != null && humidityData.getCollectedAt() != null
                && (latest == null || humidityData.getCollectedAt().isAfter(latest))) {
            latest = humidityData.getCollectedAt();
        }
        if (lightData != null && lightData.getCollectedAt() != null
                && (latest == null || lightData.getCollectedAt().isAfter(latest))) {
            latest = lightData.getCollectedAt();
        }
        return latest;
    }
}
