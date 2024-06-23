package com.mung.consumer.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = {
    "com.mung.member",
    "com.mung.product",
    "com.mung.stock",
    "com.mung.products",
    "com.mung.order",
    "com.mung.payment",
    "com.mung.kafka"})
public class BeanConfig {

}
