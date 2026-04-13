package com.plantcloud.alert.service;

import com.plantcloud.alert.vo.AlertLogVO;

import java.util.List;

public interface AlertService {

    List<AlertLogVO> getCurrentAlerts(Long plantId);

    List<AlertLogVO> getAlertLogs(Long plantId);

    void acknowledge(Long alertId, Long userId);
}
