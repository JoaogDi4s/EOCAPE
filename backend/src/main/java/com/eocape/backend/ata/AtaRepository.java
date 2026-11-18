package com.eocape.backend.ata;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Declara as consultas de persistência de atas de assembleia utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface AtaRepository extends JpaRepository<Ata, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio; ordena por data da reunião em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Ata> findByCondominioIdOrderByDataReuniaoDesc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio, ata publicada; ordena por data da reunião em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Ata> findByCondominioIdAndPublicadaTrueOrderByDataReuniaoDesc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do registro, identificador do condomínio.
     *
     * @param id Identificador único do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<Ata> findByIdAndCondominioId(UUID id, UUID condominioId);
}