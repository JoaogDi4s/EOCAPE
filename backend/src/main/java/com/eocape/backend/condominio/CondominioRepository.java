package com.eocape.backend.condominio;
import com.eocape.backend.condominio.dto.CondominioRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CondominioRepository extends JpaRepository<Condominio, UUID> {
}