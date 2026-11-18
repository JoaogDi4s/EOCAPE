package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de anexos de manutenções utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ArquivoManutencaoRepository extends JpaRepository<ArquivoManutencao, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador da manutenção.
     *
     * @param manutencaoId Identificador da manutenção.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<ArquivoManutencao> findByManutencaoId(UUID manutencaoId);

    /**
     * Verifica se existem registros pelos critérios: identificador da manutenção, identificador do arquivo.
     *
     * @param manutencaoId Identificador da manutenção.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByManutencaoIdAndArquivoId(UUID manutencaoId, UUID arquivoId);
}