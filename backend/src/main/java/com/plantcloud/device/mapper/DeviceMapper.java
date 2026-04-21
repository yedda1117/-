package com.plantcloud.device.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plantcloud.device.entity.Device;
<<<<<<< HEAD
import org.apache.ibatis.annotations.Mapper;
=======
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
>>>>>>> 6e43839fd8fe98fdc0d5be98124959d3980dcb35

@Mapper
public interface DeviceMapper extends BaseMapper<Device> {

    @Update("""
            UPDATE devices
            SET plant_id = #{plantId}
            WHERE id IN (1, 2, 3, 4, 5, 6, 7)
            """)
    int bindPlantToDeviceSet(@Param("plantId") Long plantId);
}
