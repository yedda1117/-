package com.plantcloud.strategyengine.core;

import org.springframework.stereotype.Component;

@Component
public class StrategyEngine {

    public void evaluate(Long plantId, String metricSnapshot) {
        // 策略引擎骨架，后续实现阈值告警、自动控制、去重与冷却时间。
    }
}
