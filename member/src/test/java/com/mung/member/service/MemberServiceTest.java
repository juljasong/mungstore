package com.mung.member.service;

import com.mung.common.domain.SendMailForm;
import com.mung.member.domain.Member;
import com.mung.member.domain.ResetPasswordUuid;
import com.mung.member.domain.Role;
import com.mung.member.exception.IncorrectEmailAndTelException;
import com.mung.member.repository.MemberRepository;
import com.mung.member.repository.ResetPasswordUuidRedisRepository;
import com.mung.member.request.ResetPasswordEmailRequest;
import com.mung.member.request.ResetPasswordRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock private ResetPasswordUuidRedisRepository resetPasswordUuidRedisRepository;

    @InjectMocks private MemberService memberService;

    @Test
    public void 비밀번호_재설정_메일_생성_성공() throws Exception {
        // given
        String email = "test@gmail.com";

        ResetPasswordEmailRequest resetPasswordEmailRequest = ResetPasswordEmailRequest.builder()
                .email(email)
                .tel("01011111111")
                .build();

        Member member = new Member(email, "", "", null, Role.USER, null);
        ReflectionTestUtils.setField(member, "id", 15L);

        given(memberRepository.findByEmailAndTel(anyString(), anyString()))
                .willReturn(Optional.of(member));

        // when
        SendMailForm sendMailForm = memberService.createPasswordResetMail(resetPasswordEmailRequest);

        // then
        assertEquals(email, sendMailForm.getTo());
        assertEquals("[멍스토어] 비밀번호 재설정 안내드립니다.", sendMailForm.getSubject());
    }
    
    @Test
    public void 비밀번호_재설정_메일_생성_실패() throws Exception {
        String email = "test@gmail.com";

        ResetPasswordEmailRequest resetPasswordEmailRequest = ResetPasswordEmailRequest.builder()
                .email(email)
                .tel("01011111111")
                .build();

        given(memberRepository.findByEmailAndTel(anyString(), anyString()))
                .willThrow(IncorrectEmailAndTelException.class);

        // expected
        assertThrows(IncorrectEmailAndTelException.class,
                () -> memberService.createPasswordResetMail(resetPasswordEmailRequest));

    }

    @Test
    public void 비밀번호_재설정_성공() throws Exception {
        // given
        String uuid = "abcd";
        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
                .password("password")
                .build();

        given(resetPasswordUuidRedisRepository.findById(uuid))
                .willReturn(Optional.ofNullable(ResetPasswordUuid.builder()
                        .memberId(15L)
                        .uuid(uuid)
                        .build()));

        Member member = new Member("", "", "", null, Role.USER, null);
        ReflectionTestUtils.setField(member, "id", 15L);
        given(memberRepository.findById(15L))
                .willReturn(Optional.of(member));

        given(bCryptPasswordEncoder.encode(anyString()))
                .willReturn("password");

        // when
        memberService.resetPassword(uuid, resetPasswordRequest);

        // then
        verify(resetPasswordUuidRedisRepository).delete(any());
    }

}