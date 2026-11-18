package com.eocape.backend.aviso;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Declara as consultas de persistência de comunicados utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface AvisoRepository extends JpaRepository<Aviso, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param pageable Configuração de paginação e ordenação da consulta.
     * @return página de resultados com os dados e as informações de paginação.
     */
    Page<Aviso> findByCondominioId(UUID condominioId, Pageable pageable);

    /**
     * Consulta os registros pelos critérios: identificador do registro, identificador do condomínio.
     *
     * @param id Identificador único do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<Aviso> findByIdAndCondominioId(UUID id, UUID condominioId);
}