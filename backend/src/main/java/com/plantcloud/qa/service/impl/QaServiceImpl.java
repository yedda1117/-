package com.plantcloud.qa.service.impl;

import com.plantcloud.qa.dto.QaAskRequest;
import com.plantcloud.qa.service.QaService;
import com.plantcloud.qa.vo.QaAnswerVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class QaServiceImpl implements QaService {

    @Override
    public QaAnswerVO ask(QaAskRequest request, Long userId) {
        return QaAnswerVO.builder()
                .question(request.getQuestion())
                .answer("问答模块骨架已创建，后续接入 RAG 与大模型。")
                .modelName("pending")
                .responseTimeMs(0L)
                .knowledgeSources(Collections.emptyList())
                .build();
    }

    @Override
    public List<QaAnswerVO> history(Long plantId, Long userId) {
        return Collections.emptyList();
    }
}
