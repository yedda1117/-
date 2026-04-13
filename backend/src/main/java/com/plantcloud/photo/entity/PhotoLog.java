package com.plantcloud.photo.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PhotoLog {

    private Long id;
    private Long plantId;
    private Long plantLogId;
    private LocalDate photoDate;
    private String originalImageUrl;
    private String processedImageUrl;
    private String thumbnailUrl;
    private String aiStatus;
    private BigDecimal aiConfidence;
    private String aiResultJson;
    private String remark;
    private Long createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
