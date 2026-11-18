package com.eocape.backend.condominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Transporta os dados de entrada de condominio, sujeitos às restrições declaradas nos componentes.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param nome Nome usado para identificar o registro.
 * @param endereco Endereço do condomínio.
 * @param cidade Cidade do condomínio.
 * @param uf Sigla da unidade federativa do condomínio.
 */
public record CondominioRequest(

        @NotBlank(message = "O nome é obrigatório") @Size(max = 100, message = "O nome não pode ter mais de 100 caracteres") String nome,

        @NotBlank(message = "O endereço é obrigatório") @Size(max = 100, message = "O endereço não pode ter mais de 100 caracteres") String endereco,

        @NotBlank(message = "A cidade é obrigatória") @Size(max = 100, message = "A cidade não pode ter mais de 100 caracteres") String cidade,

        @NotBlank(message = "A UF é obrigatória") @Pattern(regexp = "AC|AL|AP|AM|BA|CE|DF|ES|GO|MA|MT|MS|MG|PA|PB|PR|PE|PI|RJ|RN|RS|RO|RR|SC|SP|SE|TO", message = "UF inválida") String uf

) {
    /**
     * Inicializa CondominioRequest com os dados e as dependências necessários à operação.
     *
     * @param nome Nome usado para identificar o registro.
     * @param endereco Endereço do condomínio.
     * @param cidade Cidade do condomínio.
     * @param uf Sigla da unidade federativa do condomínio.
     */
    public CondominioRequest {
        uf = uf == null ? null : uf.trim().toUpperCase();
    }
}