package com.eocape.backend.vinculoUnidade;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VinculoUnidadeRepository extends JpaRepository<VinculoUnidade, UUID> {

    List<VinculoUnidade> findByUsuarioId(UUID usuarioId);

    List<VinculoUnidade> findByUnidadeBlocoCondominioId(UUID condominioId);

    boolean existsByUsuarioIdAndUnidadeIdAndAtivoTrue(UUID usuarioId, UUID unidadeId);

    /** Primeira unidade ativa do usuário no condomínio (usada para reservas). */
    Optional<VinculoUnidade> findFirstByUsuarioIdAndAtivoTrueAndUnidadeBlocoCondominioId(
            UUID usuarioId, UUID condominioId);

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