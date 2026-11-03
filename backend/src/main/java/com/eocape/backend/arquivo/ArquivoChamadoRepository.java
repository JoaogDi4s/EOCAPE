package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArquivoChamadoRepository extends JpaRepository<ArquivoChamado, UUID> {

    List<ArquivoChamado> findByChamadoId(UUID chamadoId);

    boolean existsByChamadoIdAndArquivoId(UUID chamadoId, UUID arquivoId);
}