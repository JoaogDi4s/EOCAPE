package com.eocape.backend.arquivo.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Transporta os dados de entrada de vincular arquivo classificado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param arquivoId Identificador do arquivo armazenado.
 */
public record VincularArquivoClassificadoRequest(
        @NotNull
        UUID arquivoId
) {}