package com.plantcloud.visualization.service.impl;

import com.plantcloud.visualization.service.VisualizationService;
import com.plantcloud.visualization.vo.CalendarDayVO;
import com.plantcloud.visualization.vo.CalendarDetailVO;
import com.plantcloud.visualization.vo.EnvironmentHistoryVO;
import com.plantcloud.visualization.vo.StrategyExecutionLogVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class VisualizationServiceImpl implements VisualizationService {

    @Override
    public EnvironmentHistoryVO getHistory(Long plantId, String metric, String startTime, String endTime) {
        return EnvironmentHistoryVO.builder()
                .plantId(plantId)
                .temperature(Collections.emptyList())
                .humidity(Collections.emptyList())
                .light(Collections.emptyList())
                .build();
    }

    @Override
    public List<CalendarDayVO> getCalendar(Long plantId, String month) {
        return Collections.emptyList();
    }

    @Override
    public CalendarDetailVO getCalendarDetail(Long plantId, String date) {
        return CalendarDetailVO.builder().date(date).build();
    }

    @Override
    public List<StrategyExecutionLogVO> getStrategyLogs(Long plantId) {
        return Collections.emptyList();
    }
}
