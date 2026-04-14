package com.plantcloud.mqtt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plantcloud.companion.entity.InteractionEvent;
import com.plantcloud.companion.mapper.InteractionEventMapper;
import com.plantcloud.device.entity.Device;
import com.plantcloud.device.mapper.DeviceMapper;
import com.plantcloud.mqtt.listener.HumanApproachMessage;
import com.plantcloud.mqtt.service.HumanApproachMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Slf4j
@Service
@RequiredArgsConstructor
public class HumanApproachMessageServiceImpl implements HumanApproachMessageService {

    private static final String EVENT_TYPE_OWNER_APPROACH = "OWNER_APPROACH";
    private static final String EVENT_TITLE_APPROACH = "主人来了";
    private static final String EVENT_CONTENT_APPROACH = "人体红外检测到主人靠近植物";
    private static final String EVENT_TITLE_LEAVE = "主人离开";
    private static final String EVENT_CONTENT_LEAVE = "人体红外检测到主人离开植物";

    private final DeviceMapper deviceMapper;
    private final InteractionEventMapper interactionEventMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void handleHumanApproach(Long deviceId, HumanApproachMessage message, String rawPayload) {
        Device device = deviceMapper.selectById(deviceId);
        if (device == null) {
            throw new IllegalArgumentException("Device not found, deviceId=" + deviceId);
        }
        if (device.getPlantId() == null) {
            throw new IllegalArgumentException("Plant binding missing for deviceId=" + deviceId);
        }

        Integer status = message.getStatus();
        if (status == null) {
            throw new IllegalArgumentException("Human approach status cannot be null, deviceId=" + deviceId);
        }
        if (shouldIgnoreEvent(deviceId, status)) {
            log.info("Ignored duplicated or invalid IS1 event. deviceId={}, plantId={}, status={}",
                    deviceId, device.getPlantId(), status);
            return;
        }

        InteractionEvent event = new InteractionEvent();
        event.setPlantId(device.getPlantId());
        event.setDeviceId(deviceId);
        event.setEventType(EVENT_TYPE_OWNER_APPROACH);
        event.setEventTitle(resolveTitle(status));
        event.setEventContent(resolveContent(status));
        event.setEventCount(1);
        event.setDetectedAt(resolveEventTime(message.getTimestamp()));
        event.setExtraData(rawPayload);
        interactionEventMapper.insert(event);

        log.info("Inserted IS1 interaction event successfully. eventId={}, plantId={}, deviceId={}, status={}",
                event.getId(), event.getPlantId(), deviceId, status);
    }

    private String resolveTitle(Integer status) {
        return Integer.valueOf(1).equals(status) ? EVENT_TITLE_APPROACH : EVENT_TITLE_LEAVE;
    }

    private String resolveContent(Integer status) {
        return Integer.valueOf(1).equals(status) ? EVENT_CONTENT_APPROACH : EVENT_CONTENT_LEAVE;
    }

    private boolean shouldIgnoreEvent(Long deviceId, Integer status) {
        InteractionEvent latestEvent = interactionEventMapper.selectOne(
                new LambdaQueryWrapper<InteractionEvent>()
                        .eq(InteractionEvent::getDeviceId, deviceId)
                        .eq(InteractionEvent::getEventType, EVENT_TYPE_OWNER_APPROACH)
                        .orderByDesc(InteractionEvent::getDetectedAt)
                        .orderByDesc(InteractionEvent::getId)
                        .last("limit 1")
        );

        if (latestEvent == null) {
            // 没有“主人来了”之前，不接收孤立的离开事件。
            return Integer.valueOf(0).equals(status);
        }

        boolean latestIsApproach = EVENT_TITLE_APPROACH.equals(latestEvent.getEventTitle());
        boolean latestIsLeave = EVENT_TITLE_LEAVE.equals(latestEvent.getEventTitle());

        if (Integer.valueOf(1).equals(status)) {
            return latestIsApproach;
        }
        if (Integer.valueOf(0).equals(status)) {
            return latestIsLeave;
        }

        return true;
    }

    private LocalDateTime resolveEventTime(Long timestamp) {
        if (timestamp == null) {
            return LocalDateTime.now();
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }
}
