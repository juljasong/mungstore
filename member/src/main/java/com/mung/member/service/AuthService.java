package com.mung.member.service;

import static org.springframework.util.StringUtils.hasText;

import com.mung.common.domain.Address;
import com.mung.common.utils.ValidateUtils;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.LoginLog;
import com.mung.member.domain.Member;
import com.mung.member.domain.RefreshToken;
import com.mung.member.domain.Role;
import com.mung.member.dto.LoginDto;
import com.mung.member.exception.AlreadyExistsEmailException;
import com.mung.member.exception.AlreadyExistsTelException;
import com.mung.member.exception.LockedAccountException;
import com.mung.member.exception.MemberNotFoundException;
import com.mung.member.exception.Unauthorized;
import com.mung.member.repository.LoginLogRepository;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.SignupRequest;
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
    public void signup(SignupRequest signupRequest) {
        checkDuplicateEmailAndTel(signupRequest);
        ValidateUtils.validatePassword(signupRequest.getPassword());
        ValidateUtils.validateEmail(signupRequest.getEmail());

        Member member = Member.builder()
            .email(signupRequest.getEmail())
            .password(passwordEncoder.encode(signupRequest.getPassword()))
            .name(signupRequest.getName())
            .tel(signupRequest.getTel())
            .role(signupRequest.getRole().equals("comp") ? Role.COMP
                : signupRequest.getRole().equals("admin") ? Role.ADMIN : Role.USER)
            .address(createAddress(signupRequest))
            .build();

        memberRepository.save(member);
    }

    private Address createAddress(SignupRequest signupRequest) {
        String zipcode = hasText(signupRequest.getZipcode()) ? signupRequest.getZipcode() : "";
        String city = hasText(signupRequest.getCity()) ? signupRequest.getCity() : "";
        String street = hasText(signupRequest.getStreet()) ? signupRequest.getStreet() : "";

        return new Address(zipcode, city, street);
    }

    @Transactional(noRollbackFor = MemberNotFoundException.class)
    public LoginDto login(LoginRequest login) {
        boolean isSuccess = false;
        try {
            Member member = memberRepository.findByEmail(login.getEmail())
                .orElseThrow(MemberNotFoundException::new);
            if (member.isLocked()) {
                throw new LockedAccountException();
            }

            if (!passwordEncoder.matches(login.getPassword(), member.getPassword())) {
                loginFail(member);
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
        jwtUtil.clearAccessAndRefreshToken(jwt);
    }

    private LoginDto loginSuccess(Member member) {
        member.resetLoginFailCount();

        return LoginDto.builder()
            .accessToken(jwtUtil.createToken(member.getId(), JwtUtil.ACCESS_EXPIRATION_TIME))
            .refreshToken(jwtUtil.createToken(member.getId(), JwtUtil.REFRESH_EXPIRATION_TIME))
            .memberId(member.getId())
            .build();
    }

    private void loginFail(Member member) {
        if (member.addLoginFailCount() > 5) {
            member.lockAccount();
        }
    }

    private void logLogin(String email, boolean isSuccess) {
        loginLogRepository.save(LoginLog.builder()
            .email(email)
            .isSuccess(isSuccess)
            .build());
    }

    private void checkDuplicateEmailAndTel(SignupRequest signupRequest) {
        if (memberRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        if (memberRepository.findByTel(signupRequest.getTel()).isPresent()) {
            throw new AlreadyExistsTelException();
        }
    }

    public LoginDto refreshAccessToken(String refreshToken) {
        RefreshToken token = jwtUtil.checkAndGetRefreshToken(refreshToken)
            .orElseThrow(Unauthorized::new);

        return LoginDto.builder()
            .accessToken(jwtUtil.createToken(token.getMemberId(), JwtUtil.ACCESS_EXPIRATION_TIME))
            .memberId(token.getMemberId())
            .build();
    }
}
