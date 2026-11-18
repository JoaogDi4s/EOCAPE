package com.eocape.backend.espaco_comum.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import com.eocape.backend.espaco_comum.EspacoComum;

/**
 * Representa espaços comuns e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public class EspacoComumResponse {

    /**
     * Representa os dados de detalhe devolvidos pela aplicação.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param id Identificador único do registro.
     * @param areaId Identificador da área comum, quando informada.
     * @param capacidade Quantidade de pessoas comportadas pelo espaço comum.
     * @param horarioAbertura Horário inicial permitido para utilização do espaço.
     * @param horarioFechamento Horário final permitido para utilização do espaço.
     * @param taxa Valor cobrado pela utilização do espaço comum.
     * @param exigeAprovacao Indica se a reserva depende de aprovação por um gestor.
     * @param regras Regras de utilização do espaço comum.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     * @param criadoEm Instante de criação do registro.
     */
    public record Detalhe(
            UUID id,
            UUID areaId,
            Integer capacidade,
            LocalTime horarioAbertura,
            LocalTime horarioFechamento,
            BigDecimal taxa,
            boolean exigeAprovacao,
            String regras,
            boolean ativo,
            Instant criadoEm) {
        /**
         * Converte a entidade de domínio para os dados expostos na resposta da API.
         *
         * @param espacoComum Espaço comum ao qual a reserva ou configuração se refere.
         * @return entidade de detalhe localizada ou persistida pela operação.
         */
        public static Detalhe from(EspacoComum espacoComum) {
            return new Detalhe(
                    espacoComum.getId(),
                    espacoComum.getArea().getId(),
                    espacoComum.getCapacidade(),
                    espacoComum.getHorarioAbertura(),
                    espacoComum.getHorarioFechamento(),
                    espacoComum.getTaxa(),
                    espacoComum.isExigeAprovacao(),
                    espacoComum.getRegras(),
                    espacoComum.isAtivo(),
                    espacoComum.getCriadoEm());
        }
    }
}