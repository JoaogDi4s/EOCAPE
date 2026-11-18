package com.eocape.backend.manutencao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de manutenções utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ManutencaoRepository extends JpaRepository<Manutencao, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Manutencao> findByCondominioId(UUID condominioId);
}