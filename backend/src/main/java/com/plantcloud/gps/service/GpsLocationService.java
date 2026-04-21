package com.plantcloud.gps.service;

import com.plantcloud.gps.entity.GpsLocationLog;

import java.util.List;

public interface GpsLocationService {

    List<GpsLocationLog> listByPlantId(Long plantId);
}
