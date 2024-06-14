package com.mung.member.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.mung.common.domain.Address;
import com.mung.common.domain.SendMailForm;
import com.mung.common.exception.InvalidPasswordException;
import com.mung.member.domain.Member;
import com.mung.member.domain.ResetPasswordUuid;
import com.mung.member.domain.Role;
import com.mung.member.exception.IncorrectEmailAndTelException;
import com.mung.member.repository.MemberRepository;
import com.mung.member.repository.ResetPasswordUuidRedisRepository;
import com.mung.member.request.ResetPasswordEmailRequest;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.UpdateMemberRequest;
import com.mung.member.response.MyPageResponse;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private ResetPasswordUuidRedisRepository resetPasswordUuidRedisRepository;

    @InjectMocks
    private MemberService memberService;

    @Test
    public void 비밀번호_재설정_메일_생성_성공() {
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
        SendMailForm sendMailForm = memberService.createPasswordResetMail(
            resetPasswordEmailRequest);

        // then
        verify(resetPasswordUuidRedisRepository).save(any());
        assertEquals(email, sendMailForm.getTo());
        assertEquals("[멍스토어] 비밀번호 재설정 안내드립니다.", sendMailForm.getSubject());
    }

    @Test
    public void 비밀번호_재설정_메일_생성_실패() {
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
    public void 비밀번호_재설정_성공() {
        // given
        String uuid = "abcd";
        ResetPasswordRequest resetPasswordRequest = ResetPasswordRequest.builder()
            .password("Test!Test1")
            .build();

        given(resetPasswordUuidRedisRepository.findById(uuid))
            .willReturn(Optional.ofNullable(ResetPasswordUuid.builder()
                .memberId(15L)
                .uuid(uuid)
                .build()));

        Member member = new Member("", "", "", null, Role.USER, new Address("", "", ""));
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

    @Test
    public void 마이페이지조회_성공() {
        // given
        Member member = new Member("test@gmail.com", "", "", null, Role.USER,
            new Address("", "", ""));
        ReflectionTestUtils.setField(member, "id", 1L);

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        // when
        MyPageResponse response = memberService.getMyPageInfo(1L);

        // then
        assertEquals(1L, response.getMemberId());
        assertEquals("test@gmail.com", response.getEmail());
    }

    @Test
    public void 회원정보수정_성공() {
        // given
        Long memberId = 15L;
        UpdateMemberRequest request = UpdateMemberRequest.builder()
            .tel("01012123232")
            .zipcode("11111")
            .city("")
            .street("")
            .build();
        Member member = new Member("test@gmail.com", "", "", null, Role.USER,
            new Address("", "", ""));
        ReflectionTestUtils.setField(member, "id", memberId);

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        // when
        Member result = memberService.updateMemberInfo(request, memberId);

        // then
        assertEquals(15L, result.getId());
        assertEquals("test@gmail.com", result.getEmail());
        assertEquals("11111", result.getAddress().getZipcode());
    }

    @Test
    public void 회원정보수정_실패_비밀번호유효성() {
        // given
        Long memberId = 15L;
        UpdateMemberRequest request = UpdateMemberRequest.builder()
            .password("test")
            .build();
        Member member = new Member("test@gmail.com", "", "", null, Role.USER,
            new Address("", "", ""));
        ReflectionTestUtils.setField(member, "id", 15L);

        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(member));

        // expected
        assertThrows(InvalidPasswordException.class,
            () -> memberService.updateMemberInfo(request, memberId));
    }

}