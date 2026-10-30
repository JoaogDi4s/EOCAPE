package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.TipoClassificado;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarClassificadoRequest(
        @NotNull
        UUID condominioId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String descricao,

        @NotNull
        TipoClassificado tipo,

        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal preco
) {
    /** Venda exige preço; doação não pode ter preço. */
    @AssertTrue(message = "Venda exige preço; doação não pode ter preço")
    public boolean isPrecoCoerente() {
        if (tipo == null) {
            return true;
        }
        return switch (tipo) {
            case VENDA -> preco != null;
            case DOACAO -> preco == null;
            case SERVICO -> true;
        };
    }
}