package com.eocape.backend.arquivo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de registrar arquivo, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param chaveArmazenamento Caminho do objeto no bucket privado de arquivos.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 */
public record RegistrarArquivoRequest(
        @NotNull
        UUID condominioId,

        @NotBlank
        @Size(max = 255)
        String nomeOriginal,

        @NotBlank
        @Size(max = 500)
        String chaveArmazenamento,

        @NotBlank
        @Size(max = 100)
        String tipoMime
) {}