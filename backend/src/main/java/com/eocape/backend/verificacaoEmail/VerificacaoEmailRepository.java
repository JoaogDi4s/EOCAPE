package com.eocape.backend.verificacaoEmail;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Declara as consultas de persistência de verificações de e-mail utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface VerificacaoEmailRepository extends JpaRepository<VerificacaoEmail, UUID> {

    /**
     * Obtém o primeiro registro pelos critérios: identificador do usuário, código ainda não utilizado; ordena por data de criação em ordem decrescente.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<VerificacaoEmail> findFirstByUsuarioIdAndUsadoEmIsNullOrderByCriadoEmDesc(UUID usuarioId);

    /**
     * Obtém o primeiro registro pelos critérios: identificador do usuário; ordena por data de criação em ordem decrescente.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<VerificacaoEmail> findFirstByUsuarioIdOrderByCriadoEmDesc(UUID usuarioId);

    /**
     * Exclui os registros pelos critérios: identificador do usuário, código ainda não utilizado.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     */
    void deleteByUsuarioIdAndUsadoEmIsNull(UUID usuarioId);
}