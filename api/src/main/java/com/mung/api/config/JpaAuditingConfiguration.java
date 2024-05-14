package com.mung.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JpaAuditingConfiguration implements AuditorAware {

//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> Optional.of("hellooooo");
//    }

    @Override
    public Optional getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        String string = authentication.getPrincipal().toString();
        System.out.println("string = " + string);
        return Optional.empty();
    }
}
