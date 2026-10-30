package com.eocape.backend.manutencao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {

    List<Manutencao> findByCondominioId(UUID condominioId);
}