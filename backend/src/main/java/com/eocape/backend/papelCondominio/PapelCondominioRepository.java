package com.eocape.backend.papelCondominio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PapelCondominioRepository extends JpaRepository<PapelCondominio, UUID> {

    List<PapelCondominio> findByUsuarioId(UUID usuarioId);

    List<PapelCondominio> findByUsuarioIdAndAtivoTrue(UUID usuarioId);

    boolean existsByUsuarioIdAndCondominioIdAndPapel(UUID usuarioId, UUID condominioId, String papel);
}