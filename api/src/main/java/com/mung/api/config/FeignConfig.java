package com.mung.api.config;

import feign.auth.BasicAuthRequestInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    private static String mailgunKey;

    @Value("${feign.mailgun.key}")
    public void setMailgunKey(String mailgunKey) {
        FeignConfig.mailgunKey = mailgunKey;
    }

    @Bean
    @Qualifier(value = "mailgun")
    public BasicAuthRequestInterceptor basicAuthenticationInterceptor() {
        return new BasicAuthRequestInterceptor("api", mailgunKey);
    }
}
