package com.eocape.backend.ata.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Transporta os dados de entrada de criar ata, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param dataReuniao Data da reunião documentada pela ata.
 * @param resumo Resumo das decisões registradas na ata.
 * @param publicada Indica se a ata pode ser consultada pelos moradores vinculados.
 */
public record CriarAtaRequest(
        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotNull
        LocalDate dataReuniao,

        @NotBlank
        String resumo,

        boolean publicada
) {}