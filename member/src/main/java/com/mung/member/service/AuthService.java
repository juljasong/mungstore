package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.LoginLog;
import com.mung.member.domain.Role;
import com.mung.member.domain.Address;
import com.mung.member.domain.Member;
import com.mung.member.exception.*;
import com.mung.member.repository.LoginLogRepository;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.Login;
import com.mung.member.request.Signup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final LoginLogRepository loginLogRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public String signup(Signup signup, String role) {

        validatePassword(signup.getPassword());
        checkDuplicateEmailAndTel(signup);

        Member member = Member.builder()
                .email(signup.getEmail())
                .password(bCryptPasswordEncoder.encode(signup.getPassword()))
                .name(signup.getName())
                .tel(signup.getTel())
                .role(role.equals("comp") ? Role.COMP : role.equals("admin") ? Role.ADMIN : Role.USER)
                .address(new Address(signup.getZipcode(), signup.getCity(), signup.getStreet()))
                .build();
            memberRepository.save(member);

        return "ok";
    }

    public String login(Login login) {
        boolean isSuccess = false;
        try {
            Member member = memberRepository.findByEmail(login.getEmail())
                    .orElseThrow(MemberNotFoundException::new);
            if (member.isLocked()) {
                throw new LockedAccount();
            }

            if (!bCryptPasswordEncoder.matches(login.getPassword(), member.getPassword())) {
                member = loginFail(member);
                throw new MemberNotFoundException();
            }

            isSuccess = true;
            return loginSuccess(member);
        } finally {
            logLogin(login.getEmail(), isSuccess);
        }
    }

    private String loginSuccess(Member member) {
        member.resetLoginFailCount();
        memberRepository.save(member);

        jwtUtil.createRefreshToken(member.getId());
        return jwtUtil.createAccessToken(member.getId());
    }

    private void logLogin(String email, boolean isSuccess) {
        loginLogRepository.save(LoginLog.builder()
                .email(email)
                .isSuccess(isSuccess)
                .build());
    }

    private Member loginFail(Member member) {
        if (member.addLoginFailCount() > 5) {
            member.lockAccount();
        }
        memberRepository.save(member);
        return member;
    }

    private void validatePassword(String password) {
        // 영문자(대,소문자), 숫자, 특수문자를 포함하여 8-15자 이내
        String regExp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";

        if (!password.matches(regExp)) {
            throw new InvalidPasswordException();
        }
    }

    private void checkDuplicateEmailAndTel(Signup signup) {
        if (memberRepository.findByEmail(signup.getEmail()).isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        if (memberRepository.findByTel(signup.getTel()).isPresent()) {
            throw new AlreadyExistsTelException();
        }
    }

}
