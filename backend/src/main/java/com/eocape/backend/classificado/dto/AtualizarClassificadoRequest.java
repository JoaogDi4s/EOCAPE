package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.TipoClassificado;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarClassificadoRequest(
        @Size(max = 200)
        String titulo,

        String descricao,

        TipoClassificado tipo,

        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal preco
) {}