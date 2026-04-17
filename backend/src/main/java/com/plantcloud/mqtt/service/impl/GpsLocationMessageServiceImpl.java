package com.plantcloud.mqtt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantcloud.device.entity.Device;
import com.plantcloud.device.mapper.DeviceMapper;
import com.plantcloud.gps.entity.GpsLocationLog;
import com.plantcloud.gps.mapper.GpsLocationLogMapper;
import com.plantcloud.mqtt.listener.GpsLocationMessage;
import com.plantcloud.mqtt.service.GpsLocationMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class GpsLocationMessageServiceImpl implements GpsLocationMessageService {
    private static final ZoneId MQTT_EVENT_ZONE = ZoneId.of("Asia/Shanghai");
    private static final long EPOCH_MILLI_THRESHOLD = 100000000000L;

    private final DeviceMapper deviceMapper;
    private final GpsLocationLogMapper gpsLocationLogMapper;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleGpsLocation(String topicDeviceToken, String rawPayload) {
        GpsLocationMessage message;
        try {
            message = objectMapper.readValue(rawPayload, GpsLocationMessage.class);
        } catch (Exception e) {
            log.warn("Failed to parse GPS location payload: {}", rawPayload, e);
            return;
        }
        Device device = resolveDevice(topicDeviceToken);
        if (device == null) {
            log.warn("Device not found for token={}", topicDeviceToken);
            return;
        }
        LocalDateTime eventTime = resolveEventTime(message.getTimestamp());
        // 可选：只在经纬度变化时插入
        GpsLocationLog lastLog = findLatestLocation(device.getId());
        if (lastLog != null &&
                lastLog.getLongitude().equals(message.getLongitude()) &&
                lastLog.getLatitude().equals(message.getLatitude())) {
            log.info("GPS位置未变动，不插入日志。deviceId={}, longitude={}, latitude={}", device.getId(), message.getLongitude(), message.getLatitude());
            return;
        }
        GpsLocationLog logEntry = new GpsLocationLog();
        logEntry.setDeviceId(device.getId());
        logEntry.setLongitude(message.getLongitude());
        logEntry.setLatitude(message.getLatitude());
        logEntry.setCreatedAt(eventTime);
        gpsLocationLogMapper.insert(logEntry);
        log.info("插入GPS位置日志成功。deviceId={}, longitude={}, latitude={}, eventTime={}", device.getId(), message.getLongitude(), message.getLatitude(), eventTime);
    }

    private Device resolveDevice(String topicDeviceToken) {
        Device device = null;
        if (isNumeric(topicDeviceToken)) {
            device = deviceMapper.selectById(Long.valueOf(topicDeviceToken));
        }
        if (device == null && StringUtils.hasText(topicDeviceToken)) {
            device = deviceMapper.selectOne(
                    new LambdaQueryWrapper<Device>()
                            .eq(Device::getDeviceCode, topicDeviceToken)
                            .last("limit 1")
            );
        }
        return device;
    }

    private GpsLocationLog findLatestLocation(Long deviceId) {
        return gpsLocationLogMapper.selectOne(
                new LambdaQueryWrapper<GpsLocationLog>()
                        .eq(GpsLocationLog::getDeviceId, deviceId)
                        .orderByDesc(GpsLocationLog::getCreatedAt)
                        .last("limit 1")
        );
    }

    private LocalDateTime resolveEventTime(Long timestamp) {
        if (timestamp == null || timestamp <= 0) {
            LocalDateTime now = LocalDateTime.now(MQTT_EVENT_ZONE);
            log.warn("GPS MQTT timestamp missing or invalid, using current time. timestamp={}, fallbackTime={}",
                    timestamp, now);
            return now;
        }
        Instant instant = timestamp >= EPOCH_MILLI_THRESHOLD
                ? Instant.ofEpochMilli(timestamp)
                : Instant.ofEpochSecond(timestamp);
        return LocalDateTime.ofInstant(instant, MQTT_EVENT_ZONE);
    }

    private boolean isNumeric(String value) {
        if (!StringUtils.hasText(value)) {
            return false;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isDigit(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
