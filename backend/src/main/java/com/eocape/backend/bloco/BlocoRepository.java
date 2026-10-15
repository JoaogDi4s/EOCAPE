package com.eocape.backend.bloco;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BlocoRepository extends JpaRepository<Bloco, UUID> {

    List<Bloco> findByCondominioId(UUID condominioId);
}