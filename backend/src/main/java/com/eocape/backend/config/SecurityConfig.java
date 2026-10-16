package com.eocape.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    // private final Environment env;

    // public SecurityConfig(Environment env) {
    //     this.env = env;
    // }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // boolean dev = env.acceptsProfiles(Profiles.of("dev"));

        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/error", "/lead").permitAll();
                    auth.requestMatchers(HttpMethod.POST, "/usuario").permitAll();
                    auth.requestMatchers("/condominio", "/condominio/**").permitAll();
                    auth.requestMatchers("/bloco","/bloco/**").permitAll();
                    auth.requestMatchers("/unidade", "/unidade/**").permitAll();
                    auth.requestMatchers("/area", "/area/**").permitAll();
                    auth.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}