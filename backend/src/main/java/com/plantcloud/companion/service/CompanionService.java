package com.plantcloud.companion.service;

import com.plantcloud.companion.vo.CompanionEventVO;

import java.util.List;

public interface CompanionService {

    List<CompanionEventVO> getEvents(Long plantId);
}
