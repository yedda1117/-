package com.plantcloud.mqtt.listener;

import org.springframework.stereotype.Component;

@Component
public class MqttUpMessageListener {

    public void onMessage(String topic, String payload) {
        // MQTT 消息接入骨架，后续接入解析、分发、入库与策略判断。
    }
}
