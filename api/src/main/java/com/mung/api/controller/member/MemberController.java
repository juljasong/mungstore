package com.mung.api.controller.member;

import com.mung.api.service.EmailSendService;
import com.mung.common.domain.SendMailForm;
import com.mung.common.response.MessageResponse;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.ResetPasswordEmailRequest;
import com.mung.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailSendService emailSendService;

    @PostMapping("/password")
    public MessageResponse sendResetPasswordEmail(@RequestBody @Valid ResetPasswordEmailRequest resetPasswordEmailRequest) throws Exception {
        SendMailForm sendMailForm = memberService.createPasswordResetMail(resetPasswordEmailRequest);
        emailSendService.sendEmail(sendMailForm);

        return MessageResponse.ofSuccess();
    }

    @PostMapping("/password/{uuid}")
    public MessageResponse resetPassword(@PathVariable String uuid, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) throws Exception {
        memberService.resetPassword(uuid, resetPasswordRequest);

        return MessageResponse.ofSuccess();
    }
}
