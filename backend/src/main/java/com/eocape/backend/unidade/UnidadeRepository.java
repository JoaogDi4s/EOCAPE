package com.eocape.backend.unidade;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de unidades utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface UnidadeRepository extends JpaRepository<Unidade, UUID> {

    /**
     * Consulta os registros pelos critérios: condomínio do bloco.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Unidade> findByBlocoCondominioId(UUID condominioId);
}