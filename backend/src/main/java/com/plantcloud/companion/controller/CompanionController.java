package com.plantcloud.companion.controller;

import com.plantcloud.common.result.Result;
import com.plantcloud.companion.service.CompanionService;
import com.plantcloud.companion.vo.CompanionEventVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/companion")
@RequiredArgsConstructor
public class CompanionController {

    private final CompanionService companionService;

    @GetMapping("/events")
    public Result<List<CompanionEventVO>> getEvents(@RequestParam Long plantId) {
        return Result.ok(companionService.getEvents(plantId));
    }
}
