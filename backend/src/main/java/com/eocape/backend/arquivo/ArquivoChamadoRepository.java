package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de anexos de chamados utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ArquivoChamadoRepository extends JpaRepository<ArquivoChamado, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do chamado.
     *
     * @param chamadoId Identificador do chamado.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<ArquivoChamado> findByChamadoId(UUID chamadoId);

    /**
     * Verifica se existem registros pelos critérios: identificador do chamado, identificador do arquivo.
     *
     * @param chamadoId Identificador do chamado.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByChamadoIdAndArquivoId(UUID chamadoId, UUID arquivoId);
}