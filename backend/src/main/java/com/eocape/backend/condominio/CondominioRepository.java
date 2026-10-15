package com.eocape.backend.condominio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CondominioRepository extends JpaRepository<Condominio, UUID> {
}