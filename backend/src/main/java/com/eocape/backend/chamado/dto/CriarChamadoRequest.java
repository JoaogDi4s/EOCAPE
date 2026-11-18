package com.eocape.backend.chamado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de criar chamado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param unidadeId Identificador da unidade habitacional.
 * @param areaId Identificador da área comum, quando informada.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param categoria Categoria usada para organizar o chamado.
 */
public record CriarChamadoRequest(
        @NotNull
        UUID unidadeId,

        UUID areaId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String descricao,

        @NotBlank
        @Size(max = 100)
        String categoria
) {}