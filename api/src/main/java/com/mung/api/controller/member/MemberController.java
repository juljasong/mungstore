package com.mung.api.controller.member;

import com.mung.api.service.EmailSendService;
import com.mung.common.domain.SendMailForm;
import com.mung.common.response.MessageResponse;
import com.mung.member.request.ResetPassword;
import com.mung.member.request.ResetPasswordEmail;
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
    public MessageResponse sendResetPasswordEmail(@RequestBody @Valid ResetPasswordEmail resetPasswordEmail) throws Exception {
        SendMailForm sendMailForm = memberService.createPasswordResetMail(resetPasswordEmail);
        emailSendService.sendEmail(sendMailForm);

        return MessageResponse.ofSuccess();
    }

    @PostMapping("/password/{uuid}")
    public MessageResponse resetPassword(@PathVariable String uuid, @RequestBody @Valid ResetPassword resetPassword) throws Exception {
        memberService.resetPassword(uuid, resetPassword);

        return MessageResponse.ofSuccess();
    }
}
