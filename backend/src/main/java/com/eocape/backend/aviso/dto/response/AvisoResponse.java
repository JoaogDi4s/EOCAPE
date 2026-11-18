package com.eocape.backend.aviso.dto.response;

import com.eocape.backend.aviso.Aviso;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de aviso devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param blocoId Identificador do bloco do condomínio.
 * @param autorId Identificador do autor do comunicado.
 * @param autorNome Nome do autor do comunicado.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param conteudo Conteúdo textual do comunicado ou bytes do arquivo, conforme o tipo declarado.
 * @param importante Indica se o comunicado deve receber destaque.
 * @param criadoEm Instante de criação do registro.
 * @param atualizadoEm Instante da última atualização do registro.
 */
public record AvisoResponse(
        UUID id,
        UUID condominioId,
        UUID blocoId,
        UUID autorId,
        String autorNome,
        String titulo,
        String conteudo,
        boolean importante,
        Instant criadoEm,
        Instant atualizadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param aviso Comunicado de origem dos dados da resposta.
     * @return dados de aviso produzidos pela operação e destinados à resposta da API.
     */
    public static AvisoResponse from(Aviso aviso) {
        return new AvisoResponse(
                aviso.getId(),
                aviso.getCondominio().getId(),
                aviso.getBloco() != null ? aviso.getBloco().getId() : null,
                aviso.getAutor().getId(),
                aviso.getAutor().getNome(),
                aviso.getTitulo(),
                aviso.getConteudo(),
                aviso.isImportante(),
                aviso.getCriadoEm(),
                aviso.getAtualizadoEm()
        );
    }
}