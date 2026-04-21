package com.plantcloud.gps.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.plantcloud.gps.entity.GpsLocationLog;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface GpsLocationLogMapper extends BaseMapper<GpsLocationLog> {

    @Select("""
            SELECT g.id,
                   g.plant_id,
                   g.device_id,
                   g.longitude,
                   g.latitude,
                   g.created_at
            FROM gps_location g
            WHERE g.plant_id = #{plantId}
            ORDER BY g.created_at DESC, g.id DESC
            """)
    List<GpsLocationLog> selectByPlantId(@Param("plantId") Long plantId);
}
