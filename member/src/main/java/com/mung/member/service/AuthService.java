package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Role;
import com.mung.member.domain.Address;
import com.mung.member.domain.Member;
import com.mung.member.exception.AlreadyExistsEmailException;
import com.mung.member.exception.MemberNotFoundException;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.Login;
import com.mung.member.request.Signup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtUtil jwtUtil;

    public String signup(Signup signup, String role) {

        Optional<Member> memberOptional = memberRepository.findByEmail(signup.getEmail());
        if (memberOptional.isPresent()) {
            throw new AlreadyExistsEmailException();
        }

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

}
