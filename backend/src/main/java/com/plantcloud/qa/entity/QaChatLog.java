package com.plantcloud.qa.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("qa_chat_logs")
@EqualsAndHashCode(callSuper = true)
public class QaChatLog extends BaseEntity {

    private Long userId;
    private Long plantId;
    private String question;
    private String answer;
    private String knowledgeSources;
    private String modelName;
    private Long responseTimeMs;
    private String status;
    private LocalDateTime createdAt;
}
