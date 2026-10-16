package com.eocape.backend.espaco_comum.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

import com.eocape.backend.espaco_comum.EspacoComum;

public class EspacoComumResponse {

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