package com.eocape.backend.chamado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistoricoChamadoRepository extends JpaRepository<HistoricoChamado, UUID> {

    List<HistoricoChamado> findByChamadoIdOrderByCriadoEmAsc(UUID chamadoId);
}