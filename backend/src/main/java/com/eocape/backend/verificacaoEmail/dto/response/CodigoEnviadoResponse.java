package com.eocape.backend.verificacaoEmail.dto.response;

import java.time.Instant;

/**
 * Representa os dados de codigo enviado devolvidos pela aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 * @param expiraEm Instante a partir do qual o código deixa de ser válido.
 */
public record CodigoEnviadoResponse(
        Instant expiraEm
) {}