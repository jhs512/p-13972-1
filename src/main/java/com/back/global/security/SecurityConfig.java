package com.back.global.security;

import com.back.global.rsData.RsData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/favicon.ico").permitAll()
                                .requestMatchers("/h2-console/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/*/posts/{id:\\d+}", "/api/*/posts", "/api/*/posts/{postId:\\d+}/comments", "/api/*/posts/{postId:\\d+}/comments/{id:\\d+}").permitAll()
                                .requestMatchers("/api/*/members/login", "/api/*/members/logout").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/members").permitAll()
                                .requestMatchers("/api/*/adm/**").hasRole("ADMIN")
                                .requestMatchers("/api/*/**").authenticated()
                                .anyRequest().permitAll()
                )
                .headers(
                        headers -> headers
                                .frameOptions(
                                        HeadersConfigurer.FrameOptionsConfig::sameOrigin
                                )
                ).csrf(
                        AbstractHttpConfigurer::disable
                )
                .addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling
                                .authenticationEntryPoint(
                                        (request, response, authException) -> {
                                            response.setContentType("application/json;charset=UTF-8");

                                            response.setStatus(401);
                                            response.getWriter().write(
                                                    objectMapper.writeValueAsString(
                                                            new RsData<Void>(
                                                                    "401-1",
                                                                    "로그인 후 이용해주세요."
                                                            )
                                                    )
                                            );
                                        }
                                )
                                .accessDeniedHandler(
                                        (request, response, accessDeniedException) -> {
                                            response.setContentType("application/json;charset=UTF-8");

                                            response.setStatus(403);
                                            response.getWriter().write(
                                                    objectMapper.writeValueAsString(
                                                            new RsData<Void>(
                                                                    "403-1",
                                                                    "권한이 없습니다."
                                                            )
                                                    )
                                            );
                                        }
                                )
                );

        return http.build();
    }
}
