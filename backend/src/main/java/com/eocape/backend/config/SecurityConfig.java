package com.eocape.backend.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.eocape.backend.auth.JwtAuthenticationFilter;
import com.eocape.backend.auth.TokenService;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, TokenService tokenService) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtAuthenticationFilter(tokenService),
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> auth

                        // Públicas
                        .requestMatchers("/error", "/lead").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuario/login").permitAll()

                        // Primeiro acesso (tokens de finalidade específica)
                        .requestMatchers(HttpMethod.POST, "/usuario/definir-senha")
                        .hasAuthority("DEFINIR_SENHA")
                        .requestMatchers("/verificacao-email/**")
                        .hasAuthority("PRIMEIRO_ACESSO")

                        // Demais rotas exigem token de acesso completo
                        .requestMatchers("/usuario/**").hasAuthority("ACESSO")
                        .requestMatchers("/api/condominios/**").hasAuthority("ACESSO")
                        .requestMatchers(
                                "/condominio/**",
                                "/bloco/**",
                                "/unidade/**",
                                "/area/**",
                                "/espaco-comum/**",
                                "/vinculo-unidade/**",
                                "/papel-condominio/**")
                        .hasAuthority("ACESSO")

                        // Catch-all: qualquer rota não listada exige ACESSO
                        .anyRequest().hasAuthority("ACESSO"));

        return http.build();
    }
}