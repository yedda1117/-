package com.plantcloud.qa.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QaAskRequest {

    @NotNull
    private Long plantId;

    @NotBlank
    private String question;
}
