package com.mung.api.config.jwt;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.AccessToken;
import com.mung.member.domain.Member;
import com.mung.member.exception.Unauthorized;
import com.mung.member.repository.MemberRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
        MemberRepository memberRepository, JwtUtil jwtUtil) {

        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }

    private static void authorization(Member member) {
        PrincipalDetails principalDetails = new PrincipalDetails(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,
            null, principalDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain chain) throws IOException, ServletException {

        String jwtHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(jwtHeader) || !jwtHeader.startsWith("Bearer")) {
            chain.doFilter(request, response);
            return;
        }

        String jwtToken = request.getHeader("Authorization").replace("Bearer ", "");
        try {
            AccessToken accessToken = jwtUtil.checkAndGetAccessToken(jwtToken)
                .orElseThrow(Unauthorized::new);

            Member member = memberRepository.findById(accessToken.getMemberId())
                .orElseThrow(Unauthorized::new);

            authorization(member);

        } catch (Exception e) {
            log.error(":: JwtAuthorizationFilter.doFilterInternal :: ", e);
        } finally {
            chain.doFilter(request, response);
        }
    }

}
