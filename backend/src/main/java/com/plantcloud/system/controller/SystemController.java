package com.plantcloud.system.controller;

import com.plantcloud.common.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/system")
@RequiredArgsConstructor
public class SystemController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("/db/ping")
    public Result<Map<String, Object>> dbPing() {
        Integer value = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("database", "plant_care_system");
        data.put("host", "localhost");
        data.put("ping", value);
        data.put("status", value != null && value == 1 ? "UP" : "DOWN");
        return Result.ok(data);
    }

    @ExceptionHandler(Exception.class)
    public Result<Map<String, Object>> handleException(Exception ex) {
        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            root = root.getCause();
        }
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("exception", ex.getClass().getName());
        data.put("message", ex.getMessage());
        data.put("rootCause", root.getClass().getName());
        data.put("rootMessage", root.getMessage());
        return Result.<Map<String, Object>>builder()
                .code(50001)
                .message("DB ping failed: " + root.getMessage())
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
