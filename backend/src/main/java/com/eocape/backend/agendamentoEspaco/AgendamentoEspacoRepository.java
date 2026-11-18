package com.eocape.backend.agendamentoEspaco;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de reservas de espaços comuns utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface AgendamentoEspacoRepository extends JpaRepository<AgendamentoEspaco, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do condomínio; ordena por data de início em ordem crescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<AgendamentoEspaco> findByCondominioIdOrderByInicioAsc(UUID condominioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio, identificador do usuário; ordena por data de início em ordem crescente.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<AgendamentoEspaco> findByCondominioIdAndUsuarioIdOrderByInicioAsc(UUID condominioId, UUID usuarioId);

    /**
     * Verifica se existe reserva do mesmo espaço com período sobreposto, desconsiderando o status indicado.
     *
     * @param espacoId Identificador de espaco.
     * @param inicio Data e hora de início da reserva.
     * @param fim Data e hora de término da reserva, posterior ao início.
     * @param statusIgnorado Status cujas reservas são desconsideradas na verificação de conflito.
     * @return true se houver sobreposição com uma reserva não ignorada; false caso contrário.
     */
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