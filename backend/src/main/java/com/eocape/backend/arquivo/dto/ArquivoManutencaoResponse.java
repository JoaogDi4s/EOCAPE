package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoManutencao;

import java.util.UUID;

/**
 * Representa os dados de arquivo manutencao devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param manutencaoId Identificador da manutenção.
 * @param arquivoId Identificador do arquivo armazenado.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 */
public record ArquivoManutencaoResponse(
        UUID id,
        UUID manutencaoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param vinculo Vínculo entre o arquivo e seu registro de origem.
     * @return dados de arquivo manutencao produzidos pela operação e destinados à resposta da API.
     */
    public static ArquivoManutencaoResponse from(ArquivoManutencao vinculo) {
        return new ArquivoManutencaoResponse(
                vinculo.getId(),
                vinculo.getManutencao().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}