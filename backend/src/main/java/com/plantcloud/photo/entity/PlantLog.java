package com.plantcloud.photo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.plantcloud.common.model.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@TableName("plant_logs")
@EqualsAndHashCode(callSuper = true)
public class PlantLog extends BaseEntity {

    private Long plantId;
    private LocalDateTime logTime;
    private String logContent;
    private LocalDateTime createdAt;
}
