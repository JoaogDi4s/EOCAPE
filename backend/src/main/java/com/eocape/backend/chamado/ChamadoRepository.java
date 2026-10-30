package com.eocape.backend.chamado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {

    List<Chamado> findByCondominioIdOrderByCriadoEmDesc(UUID condominioId);

    List<Chamado> findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(UUID condominioId, UUID usuarioId);
}