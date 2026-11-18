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

/**
 * Configura autenticação sem sessão, proteção das rotas e codificação de senhas.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Configuration
public class SecurityConfig {

    /**
     * Disponibiliza o codificador BCrypt utilizado em senhas e códigos.
     *
     * @return instância do codificador BCrypt utilizada pela aplicação.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Monta a cadeia de filtros e as regras de autorização das rotas HTTP.
     *
     * @param http Configurador das regras de segurança HTTP.
     * @param tokenService Serviço de emissão e validação dos tokens JWT.
     * @return cadeia de filtros HTTP com as regras de autenticação e autorização.
     * @throws Exception se a operação não puder ser concluída.
     */
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