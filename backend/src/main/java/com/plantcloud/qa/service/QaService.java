package com.plantcloud.qa.service;

import com.plantcloud.qa.dto.QaAskRequest;
import com.plantcloud.qa.vo.QaAnswerVO;

import java.util.List;

public interface QaService {

    QaAnswerVO ask(QaAskRequest request, Long userId);

    List<QaAnswerVO> history(Long plantId, Long userId);
}
