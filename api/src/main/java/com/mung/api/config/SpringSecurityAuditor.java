package com.mung.api.config;

import com.mung.api.config.auth.PrincipalDetails;
import java.util.Optional;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SpringSecurityAuditor implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication.getPrincipal().equals("anonymousUser")) {

            return Optional.empty();
        }

        PrincipalDetails member = (PrincipalDetails) authentication.getPrincipal();
        return Optional.of(member.getMemberId());
    }

}
