package com.eocape.backend.verificacaoEmail;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VerificacaoEmailRepository extends JpaRepository<VerificacaoEmail, UUID> {

    Optional<VerificacaoEmail> findFirstByEmailAndUsadoEmIsNullOrderByCriadoEmDesc(String email);

    void deleteByUsuarioIdAndUsadoEmIsNull(UUID usuarioId);
}