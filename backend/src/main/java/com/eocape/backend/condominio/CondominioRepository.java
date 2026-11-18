package com.eocape.backend.condominio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Declara as consultas de persistência de condomínios utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface CondominioRepository extends JpaRepository<Condominio, UUID> {
}