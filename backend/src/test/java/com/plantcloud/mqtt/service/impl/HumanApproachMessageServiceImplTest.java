package com.plantcloud.mqtt.service.impl;

import com.plantcloud.companion.entity.InteractionEvent;
import com.plantcloud.companion.mapper.InteractionEventMapper;
import com.plantcloud.device.entity.Device;
import com.plantcloud.device.mapper.DeviceMapper;
import com.plantcloud.mqtt.listener.HumanApproachMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HumanApproachMessageServiceImplTest {

    @Mock
    private DeviceMapper deviceMapper;

    @Mock
    private InteractionEventMapper interactionEventMapper;

    @InjectMocks
    private HumanApproachMessageServiceImpl humanApproachMessageService;

    @Captor
    private ArgumentCaptor<InteractionEvent> interactionEventCaptor;

    private Device pirDevice;

    @BeforeEach
    void setUp() {
        pirDevice = new Device();
        pirDevice.setId(5L);
        pirDevice.setPlantId(1L);
        pirDevice.setDeviceCode("bearpi-pir-001");
        pirDevice.setDeviceType("PIR_SENSOR");
    }

    @Test
    void shouldPersistOwnerApproachEventWhenStatusIsOne() {
        HumanApproachMessage message = new HumanApproachMessage();
        message.setEventType("human_approach");
        message.setStatus(1);
        message.setTimestamp(1713180000L);

        when(deviceMapper.selectById(5L)).thenReturn(pirDevice);
        when(interactionEventMapper.selectOne(any())).thenReturn(null);
        when(interactionEventMapper.insert(any(InteractionEvent.class))).thenReturn(1);

        humanApproachMessageService.handleHumanApproach(5L, message, "{\"status\":1}");

        verify(interactionEventMapper, times(1)).insert(interactionEventCaptor.capture());
        InteractionEvent saved = interactionEventCaptor.getValue();
        assertEquals(1L, saved.getPlantId());
        assertEquals(5L, saved.getDeviceId());
        assertEquals("OWNER_APPROACH", saved.getEventType());
        assertEquals("主人来了", saved.getEventTitle());
        assertEquals("人体红外检测到主人靠近植物", saved.getEventContent());
        assertEquals(1, saved.getEventCount());
        assertNotNull(saved.getDetectedAt());
        assertEquals("{\"status\":1}", saved.getExtraData());
    }

    @Test
    void shouldPersistOwnerLeaveEventWhenStatusIsZero() {
        HumanApproachMessage message = new HumanApproachMessage();
        message.setEventType("human_approach");
        message.setStatus(0);
        message.setTimestamp(1713180060L);

        when(deviceMapper.selectById(5L)).thenReturn(pirDevice);
        when(interactionEventMapper.selectOne(any())).thenReturn(buildLatestEvent("主人来了"));
        when(interactionEventMapper.insert(any(InteractionEvent.class))).thenReturn(1);

        humanApproachMessageService.handleHumanApproach(5L, message, "{\"status\":0}");

        verify(interactionEventMapper, times(1)).insert(interactionEventCaptor.capture());
        InteractionEvent saved = interactionEventCaptor.getValue();
        assertEquals(1L, saved.getPlantId());
        assertEquals(5L, saved.getDeviceId());
        assertEquals("OWNER_APPROACH", saved.getEventType());
        assertEquals("主人离开", saved.getEventTitle());
        assertEquals("人体红外检测到主人离开植物", saved.getEventContent());
        assertEquals(1, saved.getEventCount());
        assertNotNull(saved.getDetectedAt());
        assertEquals("{\"status\":0}", saved.getExtraData());
    }

    @Test
    void shouldIgnoreDuplicateApproachEventWhenLatestStateIsApproach() {
        HumanApproachMessage message = new HumanApproachMessage();
        message.setEventType("human_approach");
        message.setStatus(1);
        message.setTimestamp(1713180000L);

        when(deviceMapper.selectById(5L)).thenReturn(pirDevice);
        when(interactionEventMapper.selectOne(any())).thenReturn(buildLatestEvent("主人来了"));

        humanApproachMessageService.handleHumanApproach(5L, message, "{\"status\":1}");

        verify(interactionEventMapper, never()).insert(any(InteractionEvent.class));
    }

    @Test
    void shouldIgnoreDuplicateLeaveEventWhenLatestStateIsLeave() {
        HumanApproachMessage message = new HumanApproachMessage();
        message.setEventType("human_approach");
        message.setStatus(0);
        message.setTimestamp(1713180060L);

        when(deviceMapper.selectById(5L)).thenReturn(pirDevice);
        when(interactionEventMapper.selectOne(any())).thenReturn(buildLatestEvent("主人离开"));

        humanApproachMessageService.handleHumanApproach(5L, message, "{\"status\":0}");

        verify(interactionEventMapper, never()).insert(any(InteractionEvent.class));
    }

    @Test
    void shouldIgnoreFirstLeaveEventWhenNoPreviousStateExists() {
        HumanApproachMessage message = new HumanApproachMessage();
        message.setEventType("human_approach");
        message.setStatus(0);
        message.setTimestamp(1713180060L);

        when(deviceMapper.selectById(5L)).thenReturn(pirDevice);
        when(interactionEventMapper.selectOne(any())).thenReturn(null);

        humanApproachMessageService.handleHumanApproach(5L, message, "{\"status\":0}");

        verify(interactionEventMapper, never()).insert(any(InteractionEvent.class));
    }

    private InteractionEvent buildLatestEvent(String title) {
        InteractionEvent event = new InteractionEvent();
        event.setId(1L);
        event.setPlantId(1L);
        event.setDeviceId(5L);
        event.setEventType("OWNER_APPROACH");
        event.setEventTitle(title);
        return event;
    }
}
