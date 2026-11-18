package com.eocape.backend.chamado.dto;

import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.chamado.StatusChamado;

import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de chamado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param usuarioNome Nome do usuário exibido na resposta.
 * @param unidadeId Identificador da unidade habitacional.
 * @param unidadeNumero Número da unidade exibido na resposta.
 * @param areaId Identificador da área comum, quando informada.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param categoria Categoria usada para organizar o chamado.
 * @param status Estado atual do registro no seu fluxo de atendimento.
 * @param concluidoEm Instante em que o atendimento foi concluído, quando aplicável.
 * @param criadoEm Instante de criação do registro.
 * @param atualizadoEm Instante da última atualização do registro.
 */
public record ChamadoResponse(
        UUID id,
        UUID condominioId,
        UUID usuarioId,
        String usuarioNome,
        UUID unidadeId,
        String unidadeNumero,
        UUID areaId,
        String titulo,
        String descricao,
        String categoria,
        StatusChamado status,
        Instant concluidoEm,
        Instant criadoEm,
        Instant atualizadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param chamado Chamado ao qual os dados ou o anexo pertencem.
     * @return dados de chamado produzidos pela operação e destinados à resposta da API.
     */
    public static ChamadoResponse from(Chamado chamado) {
        return new ChamadoResponse(
                chamado.getId(),
                chamado.getCondominio().getId(),
                chamado.getUsuario().getId(),
                chamado.getUsuario().getNome(),
                chamado.getUnidade().getId(),
                chamado.getUnidade().getNumero(),
                chamado.getArea() != null ? chamado.getArea().getId() : null,
                chamado.getTitulo(),
                chamado.getDescricao(),
                chamado.getCategoria(),
                chamado.getStatus(),
                chamado.getConcluidoEm(),
                chamado.getCriadoEm(),
                chamado.getAtualizadoEm());
    }
}