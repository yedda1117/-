package com.plantcloud.visualization.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CalendarDetailVO {

    private String date;
    private String originalImageUrl;
    private String processedImageUrl;
    private String note;
    private String temperatureSummary;
    private String humiditySummary;
    private String lightSummary;
    private Integer alertCount;
    private Integer interactionCount;
    private Integer strategyCount;
    private List<String> strategyEvents;
    private List<String> interactionEvents;
}
