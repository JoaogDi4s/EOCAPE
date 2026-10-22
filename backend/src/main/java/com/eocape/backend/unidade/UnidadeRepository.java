package com.eocape.backend.unidade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {

    List<Unidade> findByBlocoCondominioId(UUID condominioId);
}