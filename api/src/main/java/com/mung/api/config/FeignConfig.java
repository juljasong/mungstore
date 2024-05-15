package com.mung.api.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private final static String API_KEY = "59e2fa8319226235a672b38e711904b4-32a0fef1-f747f112";

    @Bean
    @Qualifier(value = "mailgun")
    public BasicAuthRequestInterceptor basicAuthenticationInterceptor() {
        return new BasicAuthRequestInterceptor("api", API_KEY);
    }
}
