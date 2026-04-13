package com.plantcloud.alert.service.impl;

import com.plantcloud.alert.service.AlertService;
import com.plantcloud.alert.vo.AlertLogVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class AlertServiceImpl implements AlertService {

    @Override
    public List<AlertLogVO> getCurrentAlerts(Long plantId) {
        return Collections.emptyList();
    }

    @Override
    public List<AlertLogVO> getAlertLogs(Long plantId) {
        return Collections.emptyList();
    }

    @Override
    public void acknowledge(Long alertId, Long userId) {
    }
}
