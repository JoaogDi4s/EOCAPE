package com.eocape.backend.vinculoUnidade;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VinculoUnidadeRepository extends JpaRepository<VinculoUnidade, UUID> {
    List<VinculoUnidade> findByUsuarioId(UUID usuarioId);
}
