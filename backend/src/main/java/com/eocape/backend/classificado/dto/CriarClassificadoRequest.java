package com.eocape.backend.classificado.dto;

import com.eocape.backend.classificado.TipoClassificado;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Transporta os dados de entrada de criar classificado, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
 * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
 */
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
    /**
     * Venda exige preço; doação não pode ter preço.
     *
     * @return true se a condição verificada for atendida; false caso contrário.
     */
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