package com.plantcloud.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.plantcloud.**.mapper")
public class MybatisPlusConfig {
}
