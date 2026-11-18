package com.eocape.backend.vinculoUnidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Declara as consultas de persistência de vínculos entre usuários e unidades utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface VinculoUnidadeRepository extends JpaRepository<VinculoUnidade, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do usuário.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<VinculoUnidade> findByUsuarioId(UUID usuarioId);

    /**
     * Consulta os registros pelos critérios: condomínio da unidade vinculada.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<VinculoUnidade> findByUnidadeBlocoCondominioId(UUID condominioId);

    /**
     * Verifica se existem registros pelos critérios: identificador do usuário, identificador da unidade, situação ativa.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param unidadeId Identificador da unidade habitacional.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByUsuarioIdAndUnidadeIdAndAtivoTrue(UUID usuarioId, UUID unidadeId);

    /**
     * Obtém o primeiro registro pelos critérios: identificador do usuário, situação ativa, condomínio da unidade vinculada.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return registro encontrado, ou Optional vazio quando os critérios não forem atendidos.
     */
    Optional<VinculoUnidade> findFirstByUsuarioIdAndAtivoTrueAndUnidadeBlocoCondominioId(
            UUID usuarioId, UUID condominioId);

    /**
     * Verifica se existem registros pelos critérios: xists morador ativo.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @Query("""
            select count(v) > 0 from VinculoUnidade v
            where v.usuario.id = :usuarioId
              and v.usuario.ativo = true
              and v.ativo = true
              and v.unidade.bloco.condominio.id = :condominioId
            """)
    boolean existsMoradorAtivo(@Param("usuarioId") UUID usuarioId,
                               @Param("condominioId") UUID condominioId);
}