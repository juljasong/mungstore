package com.mung.api.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages = {"com.mung.member"})
@EnableJpaRepositories(basePackages = {"com.mung.member"})
public class JpaConfig {
}
