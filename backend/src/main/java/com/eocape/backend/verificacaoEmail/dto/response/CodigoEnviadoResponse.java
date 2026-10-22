package com.eocape.backend.verificacaoEmail.dto.response;

import java.time.Instant;

public record CodigoEnviadoResponse(
        Instant expiraEm
) {}