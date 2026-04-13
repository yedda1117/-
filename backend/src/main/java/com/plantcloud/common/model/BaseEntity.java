package com.plantcloud.common.model;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BaseEntity {

    @TableId
    private Long id;
}
