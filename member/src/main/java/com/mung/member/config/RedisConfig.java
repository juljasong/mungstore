package com.mung.member.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@EnableRedisRepositories
@Configuration
public class RedisConfig {

    private static String redisHost;
    private static int redisPort;

    @Value("${spring.redis.host}")
    private void setRedisHost(String redisHost) {
        RedisConfig.redisHost = redisHost;
    }

    @Value("${spring.redis.port}")
    private void setRedisPort(int redisPort) {
        RedisConfig.redisPort = redisPort;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }
}
