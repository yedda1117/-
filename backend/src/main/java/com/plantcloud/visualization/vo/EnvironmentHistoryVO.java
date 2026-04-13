package com.plantcloud.visualization.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class EnvironmentHistoryVO {

    private Long plantId;
    private List<HistoryPointVO> temperature;
    private List<HistoryPointVO> humidity;
    private List<HistoryPointVO> light;

    @Data
    @Builder
    public static class HistoryPointVO {
        private String time;
        private String value;
    }
}
