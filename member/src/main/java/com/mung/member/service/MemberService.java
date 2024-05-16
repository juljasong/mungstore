package com.mung.member.service;

import com.mung.common.domain.RedisPrefix;
import com.mung.common.domain.SendMailForm;
import com.mung.member.domain.Member;
import com.mung.member.exception.IncorrectEmailAndTelException;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.ResetPassword;
import com.mung.member.request.ResetPasswordEmail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    private static final Long uuidExpirationTime = 3600000L;

    public SendMailForm createPasswordResetMail(ResetPasswordEmail resetPasswordEmail) throws Exception {

        Member member = memberRepository.findByEmailAndTel(resetPasswordEmail.getEmail(), resetPasswordEmail.getTel())
                .orElseThrow(IncorrectEmailAndTelException::new);

        String uuid = UUID.randomUUID().toString();

        setRedisUuid(uuid, member);

        StringBuffer sb = new StringBuffer();
        String text = sb.append("아래 링크를 클릭하여 비밀번호 재설정을 진행해 주십시오.\n\n")
                .append("http://localhost:8080/member/password/").append(uuid)
                .toString();

        return SendMailForm.builder()
                .from("mungstore@gmail.com")
                .to(member.getEmail())
                .subject("[멍스토어] 비밀번호 재설정 안내드립니다.")
                .text(text)
                .build();
    }

    private void setRedisUuid(String uuid, Member member) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(RedisPrefix.RESET_PASSWORD_ + uuid, String.valueOf(member.getId()), uuidExpirationTime, TimeUnit.MILLISECONDS);
    }

    public String resetPassword(String uuid, ResetPassword resetPassword) throws Exception {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String memberId = valueOperations.get(RedisPrefix.RESET_PASSWORD_ + uuid);

        if(!StringUtils.hasText(memberId)) {
            throw new BadRequestException();
        }

        Member member = memberRepository.findById(Long.valueOf(memberId))
                .orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

        member.resetPassword(bCryptPasswordEncoder.encode(resetPassword.getPassword()));
        memberRepository.save(member);

        redisTemplate.delete(RedisPrefix.RESET_PASSWORD_ + uuid);

        return "ok";
    }
}
