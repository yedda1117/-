package com.plantcloud.qa.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class QaAnswerVO {

    private String question;
    private String answer;
    private String modelName;
    private Long responseTimeMs;
    private List<String> knowledgeSources;
}
