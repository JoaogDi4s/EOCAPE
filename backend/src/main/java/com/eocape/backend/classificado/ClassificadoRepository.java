package com.eocape.backend.classificado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClassificadoRepository extends JpaRepository<Classificado, UUID> {

    List<Classificado> findByCondominioIdOrderByCriadoEmDesc(UUID condominioId);

    List<Classificado> findByCondominioIdAndStatusOrderByCriadoEmDesc(UUID condominioId, StatusClassificado status);

    List<Classificado> findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(UUID condominioId, UUID usuarioId);
}