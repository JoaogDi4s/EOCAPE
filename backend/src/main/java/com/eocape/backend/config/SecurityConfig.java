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
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        (req, res, ex) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error", "/lead").permitAll();

                    // Públicas
                    auth.requestMatchers(HttpMethod.POST, "/usuario", "/usuario/login").permitAll();

                    // Fluxo de primeiro acesso
                    auth.requestMatchers(HttpMethod.POST, "/usuario/definir-senha").hasAuthority("DEFINIR_SENHA");
                    auth.requestMatchers("/verificacao-email/**").hasAuthority("PRIMEIRO_ACESSO");

                    // Usuário já ativo
                    auth.requestMatchers("/usuario/**").hasAuthority("ACESSO");

                    // Demais rotas (mantidas como estavam, revisar depois)
                    auth.requestMatchers("/condominio", "/condominio/**").permitAll();
                    auth.requestMatchers("/bloco", "/bloco/**").permitAll();
                    auth.requestMatchers("/unidade", "/unidade/**").permitAll();
                    auth.requestMatchers("/area", "/area/**").permitAll();
                    auth.requestMatchers("/espaco-comum", "/espaco-comum/**").permitAll();
                    auth.requestMatchers("/vinculo-unidade", "/vinculo-unidade/**").permitAll();
                    auth.requestMatchers("/papel-condominio", "/papel-condominio/**").permitAll();

                    auth.anyRequest().authenticated();
                });

        return http.build();
    }
}