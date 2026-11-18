package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.TipoClassificado;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Transporta os dados de entrada de atualizar classificado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
 * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
 */
public record AtualizarClassificadoRequest(
        @Size(max = 200)
        String titulo,

        String descricao,

        TipoClassificado tipo,

        @DecimalMin(value = "0.00", inclusive = true)
        BigDecimal preco
) {}