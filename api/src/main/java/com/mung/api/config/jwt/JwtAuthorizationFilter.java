package com.mung.api.config.jwt;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Member;
import com.mung.api.exception.Unauthorized;
import com.mung.member.repository.MemberRepository;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtUtil jwtUtil) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(jwtHeader) || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        try {
            Jws<Claims> claims = jwtUtil.parseJwtToken(jwtToken);
            Long id = Long.parseLong(claims.getPayload().get("id").toString());

            authentication(request, response, chain, id);

        } catch (ExpiredJwtException e) {
            Long id = Long.parseLong(e.getClaims().get("id").toString());

            if (jwtUtil.hasRefreshToken(id)) {
                refreshJwt(response, id);

                try {
                    authentication(request, response, chain, id);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            } else {
                throw new Unauthorized();
            }

        } catch (JwtException e) {
            throw new Unauthorized();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void authentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Long id) throws Exception {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }

    private void refreshJwt(HttpServletResponse response, Long id) {
        String accessToken = jwtUtil.createAccessToken(id);
        response.addHeader("Authorization", "Bearer " + accessToken);
    }

}
