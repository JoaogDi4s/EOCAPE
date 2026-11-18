package com.eocape.backend.manutencao.dto;

import com.eocape.backend.manutencao.TipoManutencao;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

/**
 * Transporta os dados de entrada de criar manutencao, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param unidadeId Identificador da unidade habitacional.
 * @param areaId Identificador da área comum, quando informada.
 * @param chamadoId Identificador do chamado.
 * @param titulo Título apresentado na listagem e nos detalhes do registro.
 * @param descricao Descrição detalhada do registro.
 * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
 */
public record CriarManutencaoRequest(
        @NotNull
        UUID condominioId,

        UUID unidadeId,

        UUID areaId,

        UUID chamadoId,

        @NotBlank
        @Size(max = 200)
        String titulo,

        @NotBlank
        String descricao,

        @NotNull
        TipoManutencao tipo
) {
    /**
     * O banco exige exatamente um entre unidade e área.
     *
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @AssertTrue(message = "Informe a unidade ou a área, mas não as duas")
    public boolean isVinculoUnico() {
        return (unidadeId != null) ^ (areaId != null);
    }
}