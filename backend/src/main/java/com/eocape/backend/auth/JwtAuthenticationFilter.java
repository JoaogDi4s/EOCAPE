package com.eocape.backend.auth;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Extrai o token Bearer e estabelece a autenticação utilizada nas rotas protegidas.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Serviço de emissão e validação dos tokens JWT.
     */
    private final TokenService tokenService;

    /**
     * Inicializa JwtAuthenticationFilter com os dados e as dependências necessários à operação.
     *
     * @param tokenService Serviço de emissão e validação dos tokens JWT.
     */
    public JwtAuthenticationFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    /**
     * Processa o token Bearer da requisição e continua a cadeia de filtros de segurança.
     *
     * @param request Dados de entrada recebidos e validados para a operação.
     * @param response Resposta HTTP em que o processamento escreve o resultado.
     * @param chain Cadeia de filtros que deve continuar após o processamento da autenticação.
     * @throws jakarta.servlet.ServletException se a operação não puder ser concluída.
     * @throws java.io.IOException se a operação não puder ser concluída.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            tokenService.validar(header.substring(7).trim()).ifPresent(auth -> {
                var authentication = new UsernamePasswordAuthenticationToken(
                        auth.usuarioId(),
                        null,
                        List.of(new SimpleGrantedAuthority(auth.finalidade().name())));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            });
        }

        chain.doFilter(request, response);
    }
}