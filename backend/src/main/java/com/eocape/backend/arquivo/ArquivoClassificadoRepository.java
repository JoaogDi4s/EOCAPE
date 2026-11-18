package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de anexos de classificados utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ArquivoClassificadoRepository extends JpaRepository<ArquivoClassificado, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do classificado.
     *
     * @param classificadoId Identificador do anúncio classificado.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<ArquivoClassificado> findByClassificadoId(UUID classificadoId);

    /**
     * Verifica se existem registros pelos critérios: identificador do classificado, identificador do arquivo.
     *
     * @param classificadoId Identificador do anúncio classificado.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByClassificadoIdAndArquivoId(UUID classificadoId, UUID arquivoId);
}