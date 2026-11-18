package com.eocape.backend.usuario;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Declara as consultas de persistência de usuários utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    /**
     * Verifica se existem registros pelos critérios: CPF informado.
     *
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByCpf(String cpf);

    /**
     * Verifica se existem registros pelos critérios: e-mail informado.
     *
     * @param email Endereço de e-mail do usuário.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByEmail(String email);

    /**
     * Consulta os registros pelos critérios: CPF informado.
     *
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<Usuario> findByCpf(String cpf);
}