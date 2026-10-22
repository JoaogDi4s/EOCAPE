package com.eocape.backend.espaco_comum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EspacoComumRepository extends JpaRepository<EspacoComum, UUID> {

    List<EspacoComum> findByAreaIdOrderByCriadoEmAsc(UUID areaId);

    List<EspacoComum> findByAreaCondominioId(UUID condominioId);
}
