package com.eocape.backend.chamado;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de chamados de manutenção utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ChamadoRepository extends JpaRepository<Chamado, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio; ordena por data de criação em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Chamado> findByCondominioIdOrderByCriadoEmDesc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio, identificador do usuário; ordena por data de criação em ordem decrescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<Chamado> findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(UUID condominioId, UUID usuarioId);
}