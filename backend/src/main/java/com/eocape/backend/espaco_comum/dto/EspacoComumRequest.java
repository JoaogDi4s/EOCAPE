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

/**
 * Representa espaços comuns e os dados utilizados por esse recurso do EOCAPE.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public class EspacoComumRequest {

    /**
     * Transporta os dados de entrada de criar, sujeitos às restrições declaradas nos componentes.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param areaId Identificador da área comum, quando informada.
     * @param capacidade Quantidade de pessoas comportadas pelo espaço comum.
     * @param horarioAbertura Horário inicial permitido para utilização do espaço.
     * @param horarioFechamento Horário final permitido para utilização do espaço.
     * @param taxa Valor cobrado pela utilização do espaço comum.
     * @param exigeAprovacao Indica se a reserva depende de aprovação por um gestor.
     * @param regras Regras de utilização do espaço comum.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public record Criar(
            @NotNull(message = "A área é obrigatória") UUID areaId,

            @NotNull(message = "A capacidade é obrigatória") @Positive(message = "A capacidade deve ser maior que zero") Integer capacidade,

            LocalTime horarioAbertura,

            LocalTime horarioFechamento,

            @NotNull(message = "A taxa é obrigatória") @PositiveOrZero(message = "A taxa não pode ser negativa") @Digits(integer = 8, fraction = 2, message = "A taxa deve ter até 8 dígitos inteiros e 2 decimais") BigDecimal taxa,

            Boolean exigeAprovacao,

            @NotBlank(message = "As regras são obrigatórias") @Size(max = 5000, message = "As regras devem ter no máximo 5000 caracteres") String regras,

            Boolean ativo) {

        /**
         * Verifica a coerência entre os horários de abertura e fechamento informados.
         *
         * @return true se a condição verificada for atendida; false caso contrário.
         */
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
         *
         * @param area Área comum relacionada ao espaço ou ao atendimento.
         * @return entidade de espaco comum localizada ou persistida pela operação.
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