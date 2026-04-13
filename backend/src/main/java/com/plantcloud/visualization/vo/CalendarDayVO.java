package com.plantcloud.visualization.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CalendarDayVO {

    private String date;
    private Boolean hasPhoto;
    private String thumbnailUrl;
    private Integer alertCount;
    private Integer interactionCount;
    private Integer strategyCount;
    private String note;
}
