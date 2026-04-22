package com.plantcloud.plant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "植物简要信息")
public class PlantSimpleVO {

    @Schema(description = "植物 ID", example = "1")
    private Long plantId;

    @Schema(description = "植物名称", example = "网纹草")
    private String plantName;

    @Schema(description = "植物状态，来自 plants 表", example = "ACTIVE")
    private String status;
}
