package com.plantcloud.gps.controller;

import com.plantcloud.common.result.Result;
import com.plantcloud.gps.entity.GpsLocationLog;
import com.plantcloud.gps.service.GpsLocationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Validated
@RestController
@RequestMapping("/gps")
@RequiredArgsConstructor
public class GpsLocationController {

    private final GpsLocationService gpsLocationService;

    @GetMapping("/locations")
    public Result<List<GpsLocationLog>> listLocations(@RequestParam("plantId") @NotNull Long plantId) {
        return Result.ok(gpsLocationService.listByPlantId(plantId));
    }
}
