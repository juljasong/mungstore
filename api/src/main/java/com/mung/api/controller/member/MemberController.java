package com.mung.api.controller.member;

import com.mung.api.service.EmailSendService;
import com.mung.common.domain.SendMailForm;
import com.mung.common.response.MessageResponse;
import com.mung.member.request.MemberSearchCondition;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.ResetPasswordEmailRequest;
import com.mung.member.response.MemberSearch;
import com.mung.member.response.MyPageResponse;
import com.mung.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final EmailSendService emailSendService;

    @PostMapping("/password")
    public MessageResponse<?> sendResetPasswordEmail(@RequestBody @Valid ResetPasswordEmailRequest resetPasswordEmailRequest) throws Exception {
        SendMailForm sendMailForm = memberService.createPasswordResetMail(resetPasswordEmailRequest);
        emailSendService.sendEmail(sendMailForm);

        return MessageResponse.ofSuccess();
    }

    @PostMapping("/password/{uuid}")
    public MessageResponse<?> resetPassword(@PathVariable String uuid, @RequestBody @Valid ResetPasswordRequest resetPasswordRequest) throws Exception {
        memberService.resetPassword(uuid, resetPasswordRequest);

        return MessageResponse.ofSuccess();
    }

    @GetMapping("/member/{memberId}")
    public MessageResponse<?> myPage(@PathVariable Long memberId, HttpServletRequest request) throws Exception {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");
        MyPageResponse data = memberService.getMember(memberId, jwt);

        return MessageResponse.builder()
                .data(data)
                .build();
    }

    @PostMapping("/members")
    @Secured(value = "ROLE_ADMIN")
    public Page<MemberSearch> searchMembers(@RequestBody MemberSearchCondition condition) throws Exception {
        return memberService.searchMembers(condition);
    }
}
