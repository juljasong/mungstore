package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Role;
import com.mung.member.domain.Address;
import com.mung.member.domain.Member;
import com.mung.member.exception.AlreadyExistsEmailException;
import com.mung.member.exception.AlreadyExistsTelException;
import com.mung.member.exception.InvalidPasswordException;
import com.mung.member.exception.MemberNotFoundException;
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
        Member member = memberRepository.findByEmail(login.getEmail())
                .orElseThrow(MemberNotFoundException::new);

        if(!bCryptPasswordEncoder.matches(login.getPassword(), member.getPassword())) {
            throw new MemberNotFoundException();
        }

        jwtUtil.createRefreshToken(member.getId());
        return jwtUtil.createAccessToken(member.getId());
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
