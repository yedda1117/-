package com.plantcloud.companion.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.plantcloud.companion.entity.InteractionEvent;
import com.plantcloud.companion.mapper.InteractionEventMapper;
import com.plantcloud.companion.service.CompanionService;
import com.plantcloud.companion.vo.CompanionEventVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanionServiceImpl implements CompanionService {

    private final InteractionEventMapper interactionEventMapper;

    @Override
    public List<CompanionEventVO> getEvents(Long plantId) {
        return interactionEventMapper.selectList(
                        new LambdaQueryWrapper<InteractionEvent>()
                                .eq(InteractionEvent::getPlantId, plantId)
                                .orderByDesc(InteractionEvent::getDetectedAt)
                                .orderByDesc(InteractionEvent::getId)
                ).stream()
                .map(event -> CompanionEventVO.builder()
                        .id(event.getId())
                        .eventType(event.getEventType())
                        .eventTitle(event.getEventTitle())
                        .eventContent(event.getEventContent())
                        .eventCount(event.getEventCount())
                        .detectedAt(event.getDetectedAt() != null ? event.getDetectedAt().toString() : null)
                        .build())
                .toList();
    }
}
