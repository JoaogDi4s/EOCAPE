package com.eocape.backend.espaco_comum.dto;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

import com.eocape.backend.area.Area;
import com.eocape.backend.espaco_comum.EspacoComum;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class EspacoComumRequest {

    public record Criar(
            @NotNull(message = "A área é obrigatória") UUID areaId,

            @NotNull(message = "A capacidade é obrigatória") @Positive(message = "A capacidade deve ser maior que zero") Integer capacidade,

            LocalTime horarioAbertura,

            LocalTime horarioFechamento,

            @NotNull(message = "A taxa é obrigatória") @PositiveOrZero(message = "A taxa não pode ser negativa") @Digits(integer = 8, fraction = 2, message = "A taxa deve ter até 8 dígitos inteiros e 2 decimais") BigDecimal taxa,

            Boolean exigeAprovacao,

            @NotBlank(message = "As regras são obrigatórias") @Size(max = 5000, message = "As regras devem ter no máximo 5000 caracteres") String regras,

            Boolean ativo) {

        @AssertTrue(message = "O horário de fechamento deve ser posterior ao de abertura")
        public boolean isHorariosValidos() {
            if (horarioAbertura == null || horarioFechamento == null) {
                return true;
            }
            return horarioFechamento.isAfter(horarioAbertura);
        }

        /**
         * Converte a requisição em entidade. A área deve ser buscada pelo service
         * a partir de areaId antes de chamar este método.
         */
        public EspacoComum toEntity(Area area) {
            return new EspacoComum(
                    area,
                    capacidade,
                    horarioAbertura,
                    horarioFechamento,
                    taxa,
                    exigeAprovacao == null || exigeAprovacao,
                    regras,
                    ativo == null || ativo);
        }
    }
}