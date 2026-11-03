package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoRepository extends JpaRepository<Arquivo, UUID> {

    List<Arquivo> findByCondominioIdOrderByCriadoEmDesc(UUID condominioId);

    List<Arquivo> findByCondominioIdAndEnviadoPorIdOrderByCriadoEmDesc(UUID condominioId, UUID enviadoPorId);
}