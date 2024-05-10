package com.mung.member.controller;

import com.mung.common.response.MessageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping ("/main")
    public MessageResponse main() {
        return MessageResponse.builder().message("main").build();
    }

    @GetMapping ("/user/ok")
    public MessageResponse userOk() {
        return MessageResponse.builder().message("userOk").build();
    }

    @GetMapping ("/comp/ok")
    public MessageResponse compOk() {
        return MessageResponse.builder().message("compOk").build();
    }

    @GetMapping ("/admin/ok")
    public MessageResponse adminOk() {
        return MessageResponse.builder().message("adminOk").build();
    }

}
