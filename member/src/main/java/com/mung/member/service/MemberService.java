package com.mung.member.service;

import static org.springframework.util.StringUtils.hasText;

import com.mung.common.domain.SendMailForm;
import com.mung.common.exception.BadRequestException;
import com.mung.common.exception.NotExistMemberException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Address;
import com.mung.member.domain.Member;
import com.mung.member.domain.ResetPasswordUuid;
import com.mung.member.exception.IncorrectEmailAndTelException;
import com.mung.member.exception.Unauthorized;
import com.mung.member.repository.MemberRepository;
import com.mung.member.repository.ResetPasswordUuidRedisRepository;
import com.mung.member.request.MemberSearchCondition;
import com.mung.member.request.ResetPasswordEmailRequest;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.UpdateMemberRequest;
import com.mung.member.response.MemberSearchResponse;
import com.mung.member.response.MyPageResponse;
import jakarta.persistence.EntityManager;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResetPasswordUuidRedisRepository resetPasswordUuidRedisRepository;
    private final JwtUtil jwtUtil;
    private final EntityManager em;

    public SendMailForm createPasswordResetMail(
        ResetPasswordEmailRequest resetPasswordEmailRequest) {

        Member member = memberRepository.findByEmailAndTel(resetPasswordEmailRequest.getEmail(),
                resetPasswordEmailRequest.getTel())
            .orElseThrow(IncorrectEmailAndTelException::new);

        String uuid = UUID.randomUUID().toString();

        setRedisUuid(uuid, member);

        String text = "아래 링크를 클릭하여 비밀번호 재설정을 진행해 주십시오.\n\n"
            + "http://localhost:8080/password/" + uuid;

        return SendMailForm.builder()
            .from("mungstore@gmail.com")
            .to(member.getEmail())
            .subject("[멍스토어] 비밀번호 재설정 안내드립니다.")
            .text(text)
            .build();
    }

    private void setRedisUuid(String uuid, Member member) {
        resetPasswordUuidRedisRepository.save(ResetPasswordUuid.builder()
            .uuid(uuid)
            .memberId(member.getId())
            .build());
    }

    @Transactional
    public void resetPassword(String uuid, ResetPasswordRequest resetPasswordRequest) {
        ResetPasswordUuid resetPasswordUuid = resetPasswordUuidRedisRepository.findById(uuid)
            .orElseThrow(BadRequestException::new);

        Member member = memberRepository.findById(resetPasswordUuid.getMemberId())
            .orElseThrow(NotExistMemberException::new);

        member.resetPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
        resetPasswordUuidRedisRepository.delete(resetPasswordUuid);

    }

    public MyPageResponse getMember(Long memberId, String jwt) {
        identify(memberId, jwt);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(
                () -> new NotExistMemberException(":: getMember.Member Not found :: " + memberId));
        Address address = member.getAddress();

        return MyPageResponse.builder()
            .memberId(member.getId())
            .email(member.getEmail())
            .tel(member.getTel())
            .address(new Address(
                address.getZipcode(),
                address.getCity(),
                address.getStreet()))
            .build();
    }

    @Transactional
    public Member updateMemberInfo(UpdateMemberRequest request, Long memberId, String jwt) {
        identify(memberId, jwt);

        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new NotExistMemberException(
                ":: modifyMember.Member Not found :: " + memberId));

        if (hasText(request.getPassword())) {
            member.validatePassword(request.getPassword());
            member.updatePassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }
        if (hasText(request.getTel())) {
            member.updateTel(request.getTel());
        }
        if (hasText(request.getZipcode())) {
            member.updateAddress(
                new Address(request.getZipcode(), request.getCity(), request.getStreet()));
        }

        return member;
    }

    public Page<MemberSearchResponse> searchMembers(MemberSearchCondition condition) {
        PageRequest pageRequest = PageRequest.of(condition.getPageNumber(),
            condition.getPageSize());

        return memberRepository.search(condition, pageRequest);
    }

    private void identify(Long memberId, String jwt) {
        if (!Objects.equals(memberId, jwtUtil.getMemberId(jwt))) {
            throw new Unauthorized();
        }
    }

}
