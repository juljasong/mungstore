package com.mung.payment.controller;

import com.mung.payment.entity.QTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test1() {
        QTest test = QTest.test;
        return "ok";
    }
}
