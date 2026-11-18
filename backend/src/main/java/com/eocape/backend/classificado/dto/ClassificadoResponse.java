package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.Classificado;
import com.eocape.backend.classificado.StatusClassificado;
import com.eocape.backend.classificado.TipoClassificado;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa os dados de classificado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param usuarioNome Nome do usuário exibido na resposta.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
 * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
 * @param status Estado atual do registro no seu fluxo de atendimento.
 * @param criadoEm Instante de criação do registro.
 * @param atualizadoEm Instante da última atualização do registro.
 */
public record ClassificadoResponse(
        UUID id,
        UUID condominioId,
        UUID usuarioId,
        String usuarioNome,
        String titulo,
        String descricao,
        TipoClassificado tipo,
        BigDecimal preco,
        StatusClassificado status,
        Instant criadoEm,
        Instant atualizadoEm
) {
    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param c Entidade de origem do tipo Classificado, utilizada para montar os dados da resposta.
     * @return dados de classificado produzidos pela operação e destinados à resposta da API.
     */
    public static ClassificadoResponse from(Classificado c) {
        return new ClassificadoResponse(
                c.getId(),
                c.getCondominio().getId(),
                c.getUsuario().getId(),
                c.getUsuario().getNome(),
                c.getTitulo(),
                c.getDescricao(),
                c.getTipo(),
                c.getPreco(),
                c.getStatus(),
                c.getCriadoEm(),
                c.getAtualizadoEm());
    }
}