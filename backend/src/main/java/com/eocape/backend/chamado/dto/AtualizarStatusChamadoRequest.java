package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.StatusChamado;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados de entrada de atualizar status chamado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param status Estado atual do registro no seu fluxo de atendimento.
 * @param comentario Comentário que contextualiza a alteração de status.
 */
public record AtualizarStatusChamadoRequest(
                @NotNull StatusChamado status,

                @Size(max = 500) String comentario) {
}