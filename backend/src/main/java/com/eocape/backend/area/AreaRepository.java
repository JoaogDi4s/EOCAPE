package com.eocape.backend.area;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de áreas utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface AreaRepository extends JpaRepository<Area, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio; ordena por nome em ordem crescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Area> findByCondominioIdOrderByNomeAsc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Area> findByCondominioId(UUID condominioId);
}