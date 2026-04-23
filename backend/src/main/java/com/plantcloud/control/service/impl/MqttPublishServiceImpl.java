package com.plantcloud.control.service.impl;

import com.plantcloud.config.MqttProperties;
import com.plantcloud.control.model.PublishResult;
import com.plantcloud.control.service.MqttPublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Implementation of MQTT publish service.
 * Publishes control messages to MQTT broker with QoS 1 and error handling.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MqttPublishServiceImpl implements MqttPublishService {
    
    private final MqttProperties mqttProperties;
    
    @Override
    public PublishResult publish(String topic, String payload) {
        try {
            publishWithDedicatedClient(topic, payload);
            
            log.info("Successfully published MQTT message. topic={}, payload={}", 
                    topic, truncatePayload(payload));
            
            return PublishResult.builder()
                    .success(true)
                    .build();
                    
        } catch (MqttException ex) {
            log.warn("MQTT publish failed, will retry with a new publisher client. topic={}, payload={}, reasonCode={}, error={}",
                    topic, truncatePayload(payload), ex.getReasonCode(), ex.getMessage(), ex);

            try {
                publishWithDedicatedClient(topic, payload);

                log.info("Successfully published MQTT message after retry. topic={}, payload={}",
                        topic, truncatePayload(payload));

                return PublishResult.builder()
                        .success(true)
                        .build();
            } catch (MqttException retryEx) {
                String errorMessage = describeMqttException(retryEx);
                log.error("Failed to publish MQTT message after reconnect. topic={}, payload={}, reasonCode={}, error={}",
                        topic, truncatePayload(payload), retryEx.getReasonCode(), retryEx.getMessage(), retryEx);

                return PublishResult.builder()
                        .success(false)
                        .errorMessage(errorMessage)
                        .build();
            }
        }
    }

    private void publishWithDedicatedClient(String topic, String payload) throws MqttException {
        String clientId = buildPublisherClientId();
        MqttClient mqttClient = new MqttClient(
                mqttProperties.getBrokerUrl(),
                clientId,
                new MemoryPersistence()
        );

        try {
            log.info("[CTRL] mqtt connect start broker={} clientId={}", mqttProperties.getBrokerUrl(), clientId);
            mqttClient.connect(buildConnectOptions());
            log.info("[CTRL] mqtt connect success broker={} clientId={}", mqttProperties.getBrokerUrl(), clientId);
            log.info("[CTRL] mqtt publish start topic={} payload={}", topic, payload);
            publishWithClient(mqttClient, topic, payload);
            log.info("[CTRL] mqtt publish success topic={} payload={}", topic, payload);
        } finally {
            closePublisherClient(mqttClient);
        }
    }

    private void publishWithClient(MqttClient mqttClient, String topic, String payload) throws MqttException {
        MqttMessage message = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
        message.setQos(mqttProperties.getQos());
        message.setRetained(false);
        mqttClient.publish(topic, message);
    }

    private MqttConnectOptions buildConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(false);
        options.setCleanSession(true);
        options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1_1);
        options.setConnectionTimeout(mqttProperties.getConnectionTimeout());
        options.setKeepAliveInterval(mqttProperties.getKeepAliveInterval());

        if (StringUtils.hasText(mqttProperties.getUsername())) {
            options.setUserName(mqttProperties.getUsername());
        }
        if (StringUtils.hasText(mqttProperties.getPassword())) {
            options.setPassword(mqttProperties.getPassword().toCharArray());
        }
        return options;
    }

    private String buildPublisherClientId() {
        return "pc-ctrl-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    private void closePublisherClient(MqttClient mqttClient) {
        try {
            if (mqttClient.isConnected()) {
                mqttClient.disconnect();
            }
        } catch (MqttException ex) {
            log.warn("Failed to disconnect MQTT publisher client. reasonCode={}, error={}",
                    ex.getReasonCode(), ex.getMessage());
        }

        try {
            mqttClient.close(true);
        } catch (MqttException ex) {
            log.warn("Failed to close MQTT publisher client. reasonCode={}, error={}",
                    ex.getReasonCode(), ex.getMessage());
        }
    }

    private String describeMqttException(MqttException ex) {
        String message = ex.getMessage();
        if (!StringUtils.hasText(message) || "MqttException".equals(message)) {
            message = "reasonCode=" + ex.getReasonCode();
        } else {
            message = message + ", reasonCode=" + ex.getReasonCode();
        }

        Throwable cause = ex.getCause();
        if (cause != null && StringUtils.hasText(cause.getMessage())) {
            message = message + ", cause=" + cause.getMessage();
        }

        return message;
    }
    
    /**
     * Truncates payload for logging if it exceeds 100 characters.
     */
    private String truncatePayload(String payload) {
        if (payload == null) {
            return "null";
        }
        return payload.length() > 100 ? payload.substring(0, 100) + "..." : payload;
    }
}
