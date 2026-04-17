package com.plantcloud.mqtt.service;

import com.plantcloud.mqtt.listener.GpsLocationMessage;

public interface GpsLocationMessageService {
    void handleGpsLocation(String topicDeviceToken, String rawPayload);
}
