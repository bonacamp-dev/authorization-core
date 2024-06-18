package com.bonacamp.authorization.core.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * System 환경 설정
 */
@Configuration
public class AuthorizationCoreConfig implements WebMvcConfigurer {
    /**
     * REDIS 개발
     */
    public static final String HOST_NAME = "172.27.235.104";
    public static final String PASSWORD = "root1234";
    public static final int PORT = 6379;

    /**
     * REDIS 운영
     */
    /*
    public static final String HOST_NAME = "172.27.235.159";
    public static final String PASSWORD = "root1234";
    public static final int PORT = 6379;
    */
}
