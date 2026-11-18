package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.StatusClassificado;
import jakarta.validation.constraints.NotNull;

/**
 * Transporta os dados de entrada de alterar status classificado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param status Estado atual do registro no seu fluxo de atendimento.
 */
public record AlterarStatusClassificadoRequest(
        @NotNull
        StatusClassificado status
) {}