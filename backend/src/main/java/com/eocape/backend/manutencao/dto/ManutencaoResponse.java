package com.eocape.backend.manutencao.dto;

import com.eocape.backend.manutencao.Manutencao;
import com.eocape.backend.manutencao.StatusManutencao;
import com.eocape.backend.manutencao.TipoManutencao;

import java.util.UUID;

/**
 * Representa os dados de manutencao devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param unidadeId Identificador da unidade habitacional.
 * @param areaId Identificador da área comum, quando informada.
 * @param chamadoId Identificador do chamado.
 * @param criadoPorId Identificador de criado por.
 * @param criadoPorNome Nome do usuário que criou o registro.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
 * @param status Estado atual do registro no seu fluxo de atendimento.
 */
public record ManutencaoResponse(
        UUID id,
        UUID condominioId,
        UUID unidadeId,
        UUID areaId,
        UUID chamadoId,
        UUID criadoPorId,
        String criadoPorNome,
        String titulo,
        String descricao,
        TipoManutencao tipo,
        StatusManutencao status
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param m Entidade de origem do tipo Manutencao, utilizada para montar os dados da resposta.
     * @return dados de manutencao produzidos pela operação e destinados à resposta da API.
     */
    public static ManutencaoResponse from(Manutencao m) {
        return new ManutencaoResponse(
                m.getId(),
                m.getCondominio().getId(),
                m.getUnidade() != null ? m.getUnidade().getId() : null,
                m.getArea() != null ? m.getArea().getId() : null,
                m.getChamado() != null ? m.getChamado().getId() : null,
                m.getCriadoPor().getId(),
                m.getCriadoPor().getNome(),
                m.getTitulo(),
                m.getDescricao(),
                m.getTipo(),
                m.getStatus());
    }
}