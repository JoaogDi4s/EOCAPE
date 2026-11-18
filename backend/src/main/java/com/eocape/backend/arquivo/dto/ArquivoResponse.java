package com.eocape.backend.arquivo.dto;

import com.eocape.backend.arquivo.Arquivo;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de arquivo devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param enviadoPorId Identificador do usuário que enviou o arquivo.
 * @param enviadoPorNome Nome do usuário responsável pelo envio do arquivo.
 * @param nomeOriginal Nome original do arquivo apresentado ao usuário.
 * @param chaveArmazenamento Caminho do objeto no bucket privado de arquivos.
 * @param tipoMime Tipo MIME do conteúdo armazenado.
 * @param criadoEm Instante de criação do registro.
 */
public record ArquivoResponse(
        UUID id,
        UUID condominioId,
        UUID enviadoPorId,
        String enviadoPorNome,
        String nomeOriginal,
        String chaveArmazenamento,
        String tipoMime,
        Instant criadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param a Entidade de origem do tipo Arquivo, utilizada para montar os dados da resposta.
     * @return dados de arquivo produzidos pela operação e destinados à resposta da API.
     */
    public static ArquivoResponse from(Arquivo a) {
        return new ArquivoResponse(
                a.getId(),
                a.getCondominio().getId(),
                a.getEnviadoPor().getId(),
                a.getEnviadoPor().getNome(),
                a.getNomeOriginal(),
                a.getChaveArmazenamento(),
                a.getTipoMime(),
                a.getCriadoEm());
    }
}