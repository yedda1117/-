package com.plantcloud.companion.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CompanionEventVO {

    private Long id;
    private String eventType;
    private String eventTitle;
    private String eventContent;
    private Integer eventCount;
    private String detectedAt;
}
