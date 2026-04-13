package com.plantcloud.qa.controller;

import com.plantcloud.common.result.Result;
import com.plantcloud.qa.dto.QaAskRequest;
import com.plantcloud.qa.service.QaService;
import com.plantcloud.qa.vo.QaAnswerVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/qa")
@RequiredArgsConstructor
public class QaController {

    private final QaService qaService;

    @PostMapping("/ask")
    public Result<QaAnswerVO> ask(@Valid @RequestBody QaAskRequest request, @RequestParam Long userId) {
        return Result.ok(qaService.ask(request, userId));
    }

    @GetMapping("/history")
    public Result<List<QaAnswerVO>> history(@RequestParam Long plantId, @RequestParam Long userId) {
        return Result.ok(qaService.history(plantId, userId));
    }
}
