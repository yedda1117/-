package com.plantcloud.realtime.service;

import org.springframework.stereotype.Service;

@Service
public class RealtimePushService {

    public void push(String event, Object payload) {
        // 实时推送骨架，后续统一封装 WebSocket 事件下发。
    }
}
