package com.plantcloud.visualization.service;

import com.plantcloud.visualization.vo.CalendarDayVO;
import com.plantcloud.visualization.vo.CalendarDetailVO;
import com.plantcloud.visualization.vo.EnvironmentHistoryVO;
import com.plantcloud.visualization.vo.StrategyExecutionLogVO;

import java.util.List;

public interface VisualizationService {

    EnvironmentHistoryVO getHistory(Long plantId, String metric, String startTime, String endTime);

    List<CalendarDayVO> getCalendar(Long plantId, String month);

    CalendarDetailVO getCalendarDetail(Long plantId, String date);

    List<StrategyExecutionLogVO> getStrategyLogs(Long plantId);
}
