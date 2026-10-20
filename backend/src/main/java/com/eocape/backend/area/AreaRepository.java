package com.eocape.backend.area;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AreaRepository extends JpaRepository<Area, UUID> {

    List<Area> findByCondominioIdOrderByNomeAsc(UUID condominioId);

    List<Area> findByCondominioId(UUID condominioId);
}