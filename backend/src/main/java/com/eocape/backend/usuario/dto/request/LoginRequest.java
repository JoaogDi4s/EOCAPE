package com.eocape.backend.usuario.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Transporta os dados de entrada de login, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
 * @param senha Senha em texto simples recebida para validação; não deve ser registrada em logs.
 */
public record LoginRequest(
        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        String senha
) {}