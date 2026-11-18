package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoClassificado;

import java.util.UUID;

/**
 * Representa os dados de arquivo classificado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param classificadoId Identificador do anúncio classificado.
 * @param arquivoId Identificador do arquivo armazenado.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 */
public record ArquivoClassificadoResponse(
        UUID id,
        UUID classificadoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param vinculo Vínculo entre o arquivo e seu registro de origem.
     * @return dados de arquivo classificado produzidos pela operação e destinados à resposta da API.
     */
    public static ArquivoClassificadoResponse from(ArquivoClassificado vinculo) {
        return new ArquivoClassificadoResponse(
                vinculo.getId(),
                vinculo.getClassificado().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}