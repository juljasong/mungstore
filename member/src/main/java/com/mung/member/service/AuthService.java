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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public void signup(Signup signup, String role) {
        checkDuplicateEmailAndTel(signup);

        Member member = Member.builder()
                .email(signup.getEmail())
                .password(passwordEncoder.encode(signup.getPassword()))
                .name(signup.getName())
                .tel(signup.getTel())
                .role(role.equals("comp") ? Role.COMP : role.equals("admin") ? Role.ADMIN : Role.USER)
                .address(new Address(signup.getZipcode(), signup.getCity(), signup.getStreet()))
                .build();

        memberRepository.save(member);
    }

    @Transactional(noRollbackFor = MemberNotFoundException.class)
    public String login(Login login) {
        boolean isSuccess = false;
        try {
            Member member = memberRepository.findByEmail(login.getEmail())
                    .orElseThrow(MemberNotFoundException::new);
            if (member.isLocked()) {
                throw new LockedAccountException();
            }

            if (!passwordEncoder.matches(login.getPassword(), member.getPassword())) {
                member = loginFail(member);
                throw new MemberNotFoundException();
            }

            isSuccess = true;
            return loginSuccess(member);
        } finally {
            logLogin(login.getEmail(), isSuccess);
        }
    }

    public void logout(String authorization) {
        String jwt = authorization.replace("Bearer ", "");
        jwtUtil.removeRefreshToken(jwt);
    }

    private String loginSuccess(Member member) {
        member.resetLoginFailCount();

        jwtUtil.createRefreshToken(member.getId());
        return jwtUtil.createAccessToken(member.getId());
    }

    private Member loginFail(Member member) {
        if (member.addLoginFailCount() > 5) {
            member.lockAccount();
        }

        return member;
    }

    private void logLogin(String email, boolean isSuccess) {
        loginLogRepository.save(LoginLog.builder()
                .email(email)
                .isSuccess(isSuccess)
                .build());
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
