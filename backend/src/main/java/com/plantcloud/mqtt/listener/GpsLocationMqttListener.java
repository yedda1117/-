
package com.plantcloud.mqtt.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

import com.plantcloud.mqtt.service.GpsLocationMessageService;

@Slf4j
@Component
public class GpsLocationMqttListener implements MqttCallback {
    private static final String BROKER = "tcp://192.168.20.69:1883";
    private static final String CLIENT_ID = "backend_gps_gps_listener";
    private static final String TOPIC = "device/st1/location";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private GpsLocationMessageService gpsLocationMessageService;

    private MqttClient client;

    @PostConstruct
    public void init() throws Exception {
        client = new MqttClient(BROKER, CLIENT_ID);
        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        client.setCallback(this);
        client.connect(options);
        client.subscribe(TOPIC, 1);
        log.info("Subscribed to topic: {}", TOPIC);
    }

    @Override
    public void connectionLost(Throwable cause) {
        log.warn("MQTT connection lost", cause);
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        log.info("Received GPS location message, topic={}, payload={}", topic, payload);
        gpsLocationMessageService.handleGpsLocation(topic.split("/")[1], payload);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // 不需要实现
    }
}
