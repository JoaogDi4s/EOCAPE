package com.eocape.backend.agendamentoEspaco.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Transporta os dados de entrada de criar agendamento espaco, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param espacoComumId Identificador do espaço comum reservável.
 * @param inicio Data e hora de início da reserva.
 * @param fim Data e hora de término da reserva, posterior ao início.
 * @param observacao Observação complementar da operação.
 */
public record CriarAgendamentoEspacoRequest(
        @NotNull
        UUID espacoComumId,

        @NotNull
        OffsetDateTime inicio,

        @NotNull
        OffsetDateTime fim,

        @Size(max = 500)
        String observacao
) {
    /**
     * Verifica se o término da reserva é posterior ao início quando ambas as datas são informadas.
     *
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @AssertTrue(message = "O fim deve ser posterior ao início")
    public boolean isPeriodoValido() {
        return inicio == null || fim == null || fim.isAfter(inicio);
    }
}