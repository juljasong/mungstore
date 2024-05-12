package com.mung.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.config.jwt.JwtAuthenticationFilter;
import com.mung.api.config.jwt.JwtAuthorizationFilter;
import com.mung.api.config.jwt.JwtConfig;
import com.mung.api.config.auth.PrincipalDetailsService;
import com.mung.api.config.handler.Http401Handler;
import com.mung.api.config.handler.Http403Handler;
import com.mung.member.domain.Role;
import com.mung.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity(debug = true)
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final CorsConfig corsConfig;
    private final JwtConfig jwtConfig;
    private final PrincipalDetailsService principalDetailsService;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http    .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .addFilter(corsConfig.corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationManager(), jwtConfig))
                .addFilter(new JwtAuthorizationFilter(authenticationManager(), memberRepository, jwtConfig))

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/main","/auth/signup/**").permitAll()
                        //.requestMatchers("/user").access(new WebExpressionAuthorizationManager("hasRole('ADMIN') AND hasAuthority('WRITE')"))
                        .requestMatchers("/user/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers("/comp/**").hasAnyRole(Role.COMP.name(), Role.ADMIN.name())
                        .requestMatchers("/admin/**").hasAnyRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(new Http403Handler(objectMapper))
                        .authenticationEntryPoint(new Http401Handler(objectMapper))
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        return http.build();
    }

    private AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(principalDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return new WebSecurityCustomizer() {
//            @Override
//            public void customize(WebSecurity web) {
//                web.ignoring().requestMatchers("/favicon.ico", "/error")
//                        .requestMatchers(new AntPathRequestMatcher("/h2-console/**"));
//            }
//        };
//    }

}
