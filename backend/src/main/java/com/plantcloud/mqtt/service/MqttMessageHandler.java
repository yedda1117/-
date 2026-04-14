package com.plantcloud.mqtt.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.plantcloud.mqtt.listener.Is1EventMqttListener;
import com.plantcloud.mqtt.listener.MqttUpMessageListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttMessageHandler {

    private static final Pattern DEVICE_SC2_ALERT_TOPIC_PATTERN =
            Pattern.compile("^device/(\\d+)/sc2/alert$");
    private static final Pattern DEVICE_IS1_EVENT_TOPIC_PATTERN =
            Pattern.compile("^device/(\\d+)/is1/event$");

    private final ObjectMapper objectMapper;
    private final MqttUpMessageListener mqttUpMessageListener;
    private final Is1EventMqttListener is1EventMqttListener;

    /**
     * Unified MQTT message entry. It keeps the original payload string and
     * delegates business handling to the existing listener.
     */
    public void handleMessage(String topic, String payload) {
        Long deviceId = parseDeviceId(topic);
        JsonNode payloadJson = parsePayload(payload);

        log.info("MQTT message accepted. deviceId={}, topic={}, payload={}",
                deviceId, topic, payloadJson);

        if (DEVICE_SC2_ALERT_TOPIC_PATTERN.matcher(topic).matches()) {
            mqttUpMessageListener.onMessage(topic, payload);
            return;
        }
        if (DEVICE_IS1_EVENT_TOPIC_PATTERN.matcher(topic).matches()) {
            is1EventMqttListener.onMessage(topic, payload);
            return;
        }

        throw new IllegalArgumentException("Unsupported MQTT topic: " + topic);
    }

    private Long parseDeviceId(String topic) {
        Matcher sc2Matcher = DEVICE_SC2_ALERT_TOPIC_PATTERN.matcher(topic);
        if (sc2Matcher.matches()) {
            return Long.valueOf(sc2Matcher.group(1));
        }
        Matcher is1Matcher = DEVICE_IS1_EVENT_TOPIC_PATTERN.matcher(topic);
        if (is1Matcher.matches()) {
            return Long.valueOf(is1Matcher.group(1));
        }
        log.warn("MQTT topic ignored because it does not match supported patterns. topic={}", topic);
        throw new IllegalArgumentException("Unsupported MQTT topic: " + topic);
    }

    private JsonNode parsePayload(String payload) {
        try {
            return objectMapper.readTree(payload);
        } catch (IOException ex) {
            log.error("MQTT payload is not valid JSON. payload={}", payload, ex);
            throw new IllegalArgumentException("Invalid MQTT payload: " + payload, ex);
        }
    }
}
