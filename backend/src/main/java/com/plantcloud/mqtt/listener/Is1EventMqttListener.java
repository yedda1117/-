package com.plantcloud.mqtt.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantcloud.mqtt.service.HumanApproachMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class Is1EventMqttListener {

    private static final String HUMAN_APPROACH_EVENT = "human_approach";
    private static final Pattern IS1_EVENT_TOPIC_PATTERN =
            Pattern.compile("^device/(\\d+)/is1/event$");

    private final ObjectMapper objectMapper;
    private final HumanApproachMessageService humanApproachMessageService;

    public void onMessage(String topic, String payload) {
        Matcher matcher = IS1_EVENT_TOPIC_PATTERN.matcher(topic);
        if (!matcher.matches()) {
            log.warn("MQTT message skipped because topic is not device/{id}/is1/event. topic={}", topic);
            return;
        }

        Long deviceId = Long.valueOf(matcher.group(1));
        try {
            HumanApproachMessage message = objectMapper.readValue(payload, HumanApproachMessage.class);
            log.info("Parsed IS1 event message. deviceId={}, eventType={}, status={}, timestamp={}",
                    deviceId, message.getEventType(), message.getStatus(), message.getTimestamp());
            if (HUMAN_APPROACH_EVENT.equalsIgnoreCase(message.getEventType())) {
                humanApproachMessageService.handleHumanApproach(deviceId, message, payload);
            } else {
                log.warn("IS1 event ignored because event_type is unsupported. deviceId={}, eventType={}",
                        deviceId, message.getEventType());
            }
        } catch (JsonProcessingException ex) {
            log.error("Failed to parse IS1 event payload. topic={}, payload={}", topic, payload, ex);
        } catch (Exception ex) {
            log.error("Failed to handle IS1 event. topic={}, payload={}", topic, payload, ex);
        }
    }
}
