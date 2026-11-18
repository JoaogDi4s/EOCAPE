package com.eocape.backend.chamado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de histórico dos chamados utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface HistoricoChamadoRepository extends JpaRepository<HistoricoChamado, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do chamado; ordena por data de criação em ordem crescente.
     *
     * @param chamadoId Identificador do chamado.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<HistoricoChamado> findByChamadoIdOrderByCriadoEmAsc(UUID chamadoId);
}