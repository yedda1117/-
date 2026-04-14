package com.plantcloud.mqtt.service;

import com.plantcloud.mqtt.listener.HumanApproachMessage;

public interface HumanApproachMessageService {

    void handleHumanApproach(Long deviceId, HumanApproachMessage message, String rawPayload);
}
