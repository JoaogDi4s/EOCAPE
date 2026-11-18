package com.eocape.backend.lead;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de contatos interessados utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface LeadRepository extends JpaRepository<Lead, UUID> {

    /**
     * Consulta os registros pelos critérios: e-mail informado.
     *
     * @param email Endereço de e-mail do usuário.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Lead> findByEmail(String email);
}