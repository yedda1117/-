package com.plantcloud.auth.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginVO {

    private Long userId;
    private String username;
    private String role;
    private String accessToken;
    private String refreshToken;
}
