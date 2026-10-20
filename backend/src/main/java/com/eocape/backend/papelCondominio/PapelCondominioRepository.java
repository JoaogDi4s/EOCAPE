package com.eocape.backend.papelCondominio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PapelCondominioRepository extends JpaRepository<PapelCondominio, UUID> {

    List<PapelCondominio> findByUsuarioIdAndAtivoTrue(UUID usuarioId);

    List<PapelCondominio> findByCondominioId(UUID condominioId);

    boolean existsByUsuarioIdAndCondominioIdAndPapel(UUID usuarioId, UUID condominioId, String papel);

    @Query("""
            select count(p) > 0 from PapelCondominio p
            where p.usuario.id = :usuarioId
              and p.usuario.ativo = true
              and p.condominio.id = :condominioId
              and p.papel in :papeis
              and p.ativo = true
            """)
    boolean existsGestorAtivo(@Param("usuarioId") UUID usuarioId,
                              @Param("condominioId") UUID condominioId,
                              @Param("papeis") Collection<String> papeis);
}