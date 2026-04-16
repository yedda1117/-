package com.plantcloud.companion.service.impl;

import com.plantcloud.companion.service.CompanionService;
import com.plantcloud.companion.vo.CompanionEventVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CompanionServiceImpl implements CompanionService {

    @Override
    public List<CompanionEventVO> getEvents(Long plantId) {
        return Collections.emptyList();
    }
}
