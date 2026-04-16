package com.plantcloud.auth.service.impl;

import com.plantcloud.auth.dto.LoginRequest;
import com.plantcloud.auth.service.AuthService;
import com.plantcloud.auth.vo.LoginVO;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Override
    public LoginVO login(LoginRequest request) {
        return LoginVO.builder()
                .userId(1L)
                .username(request.getUsername())
                .role("USER")
                .accessToken("mock-access-token")
                .refreshToken("mock-refresh-token")
                .build();
    }
}
