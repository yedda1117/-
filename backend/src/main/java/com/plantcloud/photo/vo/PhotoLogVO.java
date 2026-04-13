package com.plantcloud.photo.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PhotoLogVO {

    private Long id;
    private String date;
    private String originalImageUrl;
    private String processedImageUrl;
    private String thumbnailUrl;
    private String aiStatus;
    private String note;
}
