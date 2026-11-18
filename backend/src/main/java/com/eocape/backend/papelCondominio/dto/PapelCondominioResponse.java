package com.eocape.backend.papelCondominio.dto;

import java.time.Instant;
import java.util.UUID;

import com.eocape.backend.papelCondominio.PapelCondominio;

/**
 * Representa os dados de papel condominio devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param id Identificador único do registro.
 * @param usuarioId Identificador do usuário envolvido na operação.
 * @param condominioId Identificador do condomínio ao qual a operação está restrita.
 * @param papel Papel de autorização atribuído ao usuário no condomínio.
 * @param ativo Indica se o registro está ativo para uso no sistema.
 * @param criadoEm Instante de criação do registro.
 */
public record PapelCondominioResponse(
        UUID id,
        UUID usuarioId,
        UUID condominioId,
        String papel,
        boolean ativo,
        Instant criadoEm) {

    /**
     * Converte a entidade de domínio para os dados expostos na resposta da API.
     *
     * @param papelCondominio Atribuição de papel de um usuário em um condomínio.
     * @return dados de papel condominio produzidos pela operação e destinados à resposta da API.
     */
    public static PapelCondominioResponse from(PapelCondominio papelCondominio) {
        return new PapelCondominioResponse(
                papelCondominio.getId(),
                papelCondominio.getUsuario().getId(),
                papelCondominio.getCondominio().getId(),
                papelCondominio.getPapel(),
                papelCondominio.isAtivo(),
                papelCondominio.getCriadoEm());
    }
}