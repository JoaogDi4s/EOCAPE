package com.eocape.backend.ata;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AtaRepository extends JpaRepository<Ata, UUID> {

    List<Ata> findByCondominioIdOrderByDataReuniaoDesc(UUID condominioId);

    List<Ata> findByCondominioIdAndPublicadaTrueOrderByDataReuniaoDesc(UUID condominioId);

    Optional<Ata> findByIdAndCondominioId(UUID id, UUID condominioId);
}