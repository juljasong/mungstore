package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class CartServiceTest {

    @Autowired
    CartService cartService;

    @MockBean
    JwtUtil jwtUtil;


}