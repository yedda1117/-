package com.plantcloud.alert.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertLogVO {

    private Long id;
    private String alertType;
    private String severity;
    private String title;
    private String content;
    private String status;
    private String createdAt;
}
