package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoAta;

import java.util.UUID;

/**
 * Representa os dados de arquivo ata devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param ataId Identificador da ata de assembleia.
 * @param arquivoId Identificador do arquivo armazenado.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 */
public record ArquivoAtaResponse(
        UUID id,
        UUID ataId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param vinculo Vínculo entre o arquivo e seu registro de origem.
     * @return dados de arquivo ata produzidos pela operação e destinados à resposta da API.
     */
    public static ArquivoAtaResponse from(ArquivoAta vinculo) {
        return new ArquivoAtaResponse(
                vinculo.getId(),
                vinculo.getAta().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}