package com.plantcloud.auth.service;

import com.plantcloud.auth.dto.LoginRequest;
import com.plantcloud.auth.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest request);
}
