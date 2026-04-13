package com.plantcloud.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(40001, "参数校验失败"),
    UNAUTHORIZED(40101, "未登录或令牌失效"),
    FORBIDDEN(40301, "无权限访问"),
    NOT_FOUND(40401, "资源不存在"),
    CONFLICT(40901, "资源状态冲突"),
    SYSTEM_ERROR(50001, "系统异常"),
    DEVICE_OFFLINE(51001, "设备离线"),
    MQTT_PUBLISH_FAILED(51002, "MQTT 指令下发失败"),
    AI_PROCESS_FAILED(52001, "AI 处理失败"),
    QA_FAILED(53001, "智能问答失败");

    private final Integer code;
    private final String message;
}
