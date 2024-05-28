package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.*;
import com.mung.member.dto.LoginDto;
import com.mung.member.exception.*;
import com.mung.member.repository.LoginLogRepository;
import com.mung.member.repository.MemberLogRepository;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.SignupRequest;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.util.StringUtils.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final MemberLogRepository memberLogRepository;
    private final LoginLogRepository loginLogRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EntityManager em;

    @Transactional
    public void signup(SignupRequest signupRequest) throws Exception {
        checkDuplicateEmailAndTel(signupRequest);

        Member member = Member.builder()
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .name(signupRequest.getName())
                .tel(signupRequest.getTel())
                .role(signupRequest.getRole().equals("comp") ? Role.COMP : signupRequest.getRole().equals("admin") ? Role.ADMIN : Role.USER)
                .address(createAddress(signupRequest))
                .build();
        Member saved = memberRepository.save(member);

        logMember(saved.getId());
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

    public void logout(String authorization) throws BadRequestException {
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

    private void checkDuplicateEmailAndTel(SignupRequest signupRequest) {
        if (memberRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        if (memberRepository.findByTel(signupRequest.getTel()).isPresent()) {
            throw new AlreadyExistsTelException();
        }
    }

    public LoginDto refreshAccessToken(String refreshToken) throws BadRequestException {
        RefreshToken token = jwtUtil.checkAndGetRefreshToken(refreshToken)
                .orElseThrow(Unauthorized::new);

        return LoginDto.builder()
                .accessToken(jwtUtil.createToken(token.getMemberId(), JwtUtil.ACCESS_EXPIRATION_TIME))
                .memberId(token.getMemberId())
                .build();
    }

    private void logMember(Long memberId) throws Exception {
        em.flush();
        em.clear();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(Exception::new);
        Address address = member.getAddress();
        memberLogRepository.save(MemberLog.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .tel(member.getTel())
                .role(member.getRole())
                .address(new Address(address.getZipcode(), address.getCity(), address.getStreet()))
                .loginFailCount(member.getLoginFailCount())
                .isLocked(member.isLocked())
                .createdAt(member.getCreatedAt())
                .createdBy(member.getCreatedBy())
                .lastModifiedAt(member.getLastModifiedAt())
                .lastModifiedBy(member.getLastModifiedBy())
                .build());
    }
}
