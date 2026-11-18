package com.eocape.backend.espaco_comum;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de espaços comuns utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface EspacoComumRepository extends JpaRepository<EspacoComum, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador da área; ordena por data de criação em ordem crescente.
     *
     * @param areaId Identificador da área comum, quando informada.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<EspacoComum> findByAreaIdOrderByCriadoEmAsc(UUID areaId);

    /**
     * Consulta os registros pelos critérios: condomínio da área.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<EspacoComum> findByAreaCondominioId(UUID condominioId);
}
