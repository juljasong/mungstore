package com.mung.api.config.jwt;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.member.domain.Member;
import com.mung.api.exception.Unauthorized;
import com.mung.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
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

@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private MemberRepository memberRepository;

    private final JwtConfig jwtConfig;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, MemberRepository memberRepository, JwtConfig jwtConfig) {
        super(authenticationManager);
        this.memberRepository = memberRepository;
        this.jwtConfig = jwtConfig;
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
            //log.info(">>>> " + jwtConfig.key);
            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(jwtConfig.getKeyFromBase64EncodedKey(jwtConfig.key))
                    .build()
                    .parseSignedClaims(jwtToken);

            Long id = Long.parseLong(claims.getPayload().get("id").toString());
            Member member = memberRepository.findById(id)
                    .orElseThrow(() -> new Exception("존재하지 않는 회원입니다."));

            PrincipalDetails principalDetails = new PrincipalDetails(member);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            chain.doFilter(request, response);
        } catch (JwtException e) {
            throw new Unauthorized();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
