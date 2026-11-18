package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de arquivos utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ArquivoRepository extends JpaRepository<Arquivo, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio; ordena por data de criação em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Arquivo> findByCondominioIdOrderByCriadoEmDesc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio, identificador de quem enviou o arquivo; ordena por data de criação em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param enviadoPorId Identificador do usuário que enviou o arquivo.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Arquivo> findByCondominioIdAndEnviadoPorIdOrderByCriadoEmDesc(UUID condominioId, UUID enviadoPorId);
}