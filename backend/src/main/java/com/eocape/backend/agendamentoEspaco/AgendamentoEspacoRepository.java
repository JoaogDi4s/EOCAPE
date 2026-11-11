package com.eocape.backend.agendamentoEspaco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AgendamentoEspacoRepository extends JpaRepository<AgendamentoEspaco, UUID> {

    List<AgendamentoEspaco> findByCondominioIdOrderByInicioAsc(UUID condominioId);

    List<AgendamentoEspaco> findByCondominioIdAndUsuarioIdOrderByInicioAsc(UUID condominioId, UUID usuarioId);

    @Query("""
            select count(a) > 0 from AgendamentoEspaco a
            where a.espacoComum.id = :espacoId
              and a.status <> :statusIgnorado
              and a.inicio < :fim
              and a.fim > :inicio
            """)
    boolean existeConflito(@Param("espacoId") UUID espacoId,
                           @Param("inicio") Instant inicio,
                           @Param("fim") Instant fim,
                           @Param("statusIgnorado") StatusAgendamento statusIgnorado);
}