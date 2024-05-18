package com.mung.member.service;

import com.mung.common.domain.SendMailForm;
import com.mung.member.domain.Member;
import com.mung.member.domain.ResetPasswordUuid;
import com.mung.member.exception.IncorrectEmailAndTelException;
import com.mung.member.repository.MemberRepository;
import com.mung.member.repository.ResetPasswordUuidRedisRepository;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.ResetPasswordEmailRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ResetPasswordUuidRedisRepository resetPasswordUuidRedisRepository;

    public SendMailForm createPasswordResetMail(ResetPasswordEmailRequest resetPasswordEmailRequest) throws Exception {

        Member member = memberRepository.findByEmailAndTel(resetPasswordEmailRequest.getEmail(), resetPasswordEmailRequest.getTel())
                .orElseThrow(IncorrectEmailAndTelException::new);

        String uuid = UUID.randomUUID().toString();

        setRedisUuid(uuid, member);

        StringBuffer sb = new StringBuffer();
        String text = sb.append("아래 링크를 클릭하여 비밀번호 재설정을 진행해 주십시오.\n\n")
                .append("http://localhost:8080/password/").append(uuid)
                .toString();

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
    public void resetPassword(String uuid, ResetPasswordRequest resetPasswordRequest) throws Exception {
        ResetPasswordUuid resetPasswordUuid = resetPasswordUuidRedisRepository.findById(uuid)
                .orElseThrow(BadRequestException::new);

        Member member = memberRepository.findById(resetPasswordUuid.getMemberId())
                .orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

        member.resetPassword(bCryptPasswordEncoder.encode(resetPasswordRequest.getPassword()));
        resetPasswordUuidRedisRepository.delete(resetPasswordUuid);
    }

}
