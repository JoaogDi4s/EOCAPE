package com.eocape.backend.verificacaoEmail;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificacaoEmailRepository extends JpaRepository<VerificacaoEmail, UUID> {

    Optional<VerificacaoEmail> findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(UUID usuarioId);

    void deleteByUsuarioIdAndUsadoEmIsNull(UUID usuarioId);
}