package com.mung.api.controller;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.member.domain.Member;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import org.springframework.test.util.ReflectionTestUtils;

public class MockSecurityContextFactory implements WithSecurityContextFactory<MockMember> {
    @Override
    public SecurityContext createSecurityContext(MockMember annotation) {
        Member mockMember = Member.builder()
                .name(annotation.name())
                .role(annotation.role())
                .build();
        ReflectionTestUtils.setField(mockMember, "id", annotation.id());

        PrincipalDetails principalDetails = new PrincipalDetails(mockMember);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(authentication);
        return context;
    }
}
