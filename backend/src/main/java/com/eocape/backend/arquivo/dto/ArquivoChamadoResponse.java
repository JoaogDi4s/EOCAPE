package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.ArquivoChamado;

import java.util.UUID;

/**
 * Representa os dados de arquivo chamado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param chamadoId Identificador do chamado.
 * @param arquivoId Identificador do arquivo armazenado.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 */
public record ArquivoChamadoResponse(
        UUID id,
        UUID chamadoId,
        UUID arquivoId,
        String nomeOriginal,
        String tipoMime
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param vinculo Vínculo entre o arquivo e seu registro de origem.
     * @return dados de arquivo chamado produzidos pela operação e destinados à resposta da API.
     */
    public static ArquivoChamadoResponse from(ArquivoChamado vinculo) {
        return new ArquivoChamadoResponse(
                vinculo.getId(),
                vinculo.getChamado().getId(),
                vinculo.getArquivo().getId(),
                vinculo.getArquivo().getNomeOriginal(),
                vinculo.getArquivo().getTipoMime());
    }
}