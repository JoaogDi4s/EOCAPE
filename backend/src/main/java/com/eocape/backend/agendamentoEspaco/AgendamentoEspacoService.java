package com.eocape.backend.agendamentoEspaco;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.agendamentoEspaco.dto.AgendamentoEspacoResponse;
import com.eocape.backend.agendamentoEspaco.dto.CriarAgendamentoEspacoRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.espaco_comum.EspacoComum;
import com.eocape.backend.espaco_comum.EspacoComumRepository;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidade;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de reservas de espaços comuns.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class AgendamentoEspacoService {

    /**
     * Fuso horário usado para comparar a reserva com o horário local de funcionamento.
     */
    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    /**
     * Repositório de persistência de reservas de espaços comuns.
     */
    private final AgendamentoEspacoRepository repository;
    /**
     * Repositório de persistência de espaços comuns.
     */
    private final EspacoComumRepository espacoRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculoRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria reservas de espaços comuns.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de agendamento espaco produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws IllegalStateException se ocorrer a condição: Horário já reservado para este espaço.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: A reserva deve estar dentro do horário de funcionamento: ;  às .
     */
    @Transactional
    public AgendamentoEspacoResponse criar(UUID solicitanteId, CriarAgendamentoEspacoRequest request) {
        EspacoComum espaco = espacoRepository.findById(request.espacoComumId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Espaço comum não encontrado: " + request.espacoComumId()));

        Condominio condominio = espaco.getArea().getCondominio();
        acesso.exigirVinculo(solicitanteId, condominio.getId());

        if (!espaco.isAtivo()) {
            throw new IllegalStateException("Espaço indisponível para reserva");
        }

        Instant inicio = request.inicio().toInstant();
        Instant fim = request.fim().toInstant();

        validarHorario(espaco, inicio, fim);

        if (repository.existeConflito(espaco.getId(), inicio, fim, StatusAgendamento.CANCELADO)) {
            throw new IllegalStateException("Horário já reservado para este espaço");
        }

        Usuario usuario = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Unidade unidade = buscarUnidadeDoUsuario(solicitanteId, condominio.getId());

        StatusAgendamento status = espaco.isExigeAprovacao()
                ? StatusAgendamento.PENDENTE
                : StatusAgendamento.CONFIRMADO;

        AgendamentoEspaco agendamento = new AgendamentoEspaco(
                condominio, espaco, usuario, unidade, inicio, fim, request.observacao(), status);

        return AgendamentoEspacoResponse.from(repository.saveAndFlush(agendamento));
    }

    /**
     * Morador vê só os próprios; gestor vê todos do condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<AgendamentoEspacoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<AgendamentoEspaco> agendamentos = acesso.isGestor(solicitanteId, condominioId)
                ? repository.findByCondominioIdOrderByInicioAsc(condominioId)
                : repository.findByCondominioIdAndUsuarioIdOrderByInicioAsc(condominioId, solicitanteId);

        return agendamentos.stream().map(AgendamentoEspacoResponse::from).toList();
    }

    /**
     * Busca pelo identificador reservas de espaços comuns.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de agendamento espaco produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar as suas reservas.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     */
    @Transactional(readOnly = true)
    public AgendamentoEspacoResponse buscarPorId(UUID solicitanteId, UUID id) {
        AgendamentoEspaco agendamento = buscar(id);
        UUID condominioId = agendamento.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = agendamento.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode visualizar as suas reservas");
        }
        return AgendamentoEspacoResponse.from(agendamento);
    }

    /**
     * Somente gestor confirma uma reserva pendente.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de agendamento espaco produzidos pela operação e destinados à resposta da API.
     * @throws IllegalStateException se ocorrer a condição: Só é possível aprovar reservas pendentes.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public AgendamentoEspacoResponse aprovar(UUID solicitanteId, UUID id) {
        AgendamentoEspaco agendamento = buscar(id);
        acesso.exigirGestor(solicitanteId, agendamento.getCondominio().getId());

        if (agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            throw new IllegalStateException("Só é possível aprovar reservas pendentes");
        }

        agendamento.alterarStatus(StatusAgendamento.CONFIRMADO);
        return AgendamentoEspacoResponse.from(repository.saveAndFlush(agendamento));
    }

    /**
     * O próprio morador ou um gestor pode cancelar.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de agendamento espaco produzidos pela operação e destinados à resposta da API.
     * @throws IllegalStateException se ocorrer a condição: Esta reserva não pode mais ser cancelada.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public AgendamentoEspacoResponse cancelar(UUID solicitanteId, UUID id) {
        AgendamentoEspaco agendamento = buscar(id);
        UUID condominioId = agendamento.getCondominio().getId();

        boolean ehDono = agendamento.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, condominioId);
        }

        if (agendamento.getStatus() == StatusAgendamento.CANCELADO
                || agendamento.getStatus() == StatusAgendamento.CONCLUIDO) {
            throw new IllegalStateException("Esta reserva não pode mais ser cancelada");
        }

        agendamento.alterarStatus(StatusAgendamento.CANCELADO);
        return AgendamentoEspacoResponse.from(repository.saveAndFlush(agendamento));
    }

    /**
     * Unidade ativa do usuário no condomínio. Quem não tem vínculo não consegue reservar,
     * porque a reserva precisa estar associada a uma unidade.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return entidade de unidade localizada ou persistida pela operação.
     * @throws IllegalStateException se ocorrer a condição: É preciso ter uma unidade vinculada no condomínio para reservar espaços.
     */
    private Unidade buscarUnidadeDoUsuario(UUID usuarioId, UUID condominioId) {
        return vinculoRepository
                .findFirstByUsuarioIdAndAtivoTrueAndUnidadeBlocoCondominioId(usuarioId, condominioId)
                .map(VinculoUnidade::getUnidade)
                .orElseThrow(() -> new IllegalStateException(
                        "É preciso ter uma unidade vinculada no condomínio para reservar espaços"));
    }

    /**
     * Horário de funcionamento do espaço, em horário de Brasília, no mesmo dia.
     *
     * @param espaco Espaço comum cuja disponibilidade e horários serão verificados.
     * @param inicio Data e hora de início da reserva.
     * @param fim Data e hora de término da reserva, posterior ao início.
     * @throws IllegalArgumentException se ocorrer a condição: A reserva deve estar dentro do horário de funcionamento: ;  às .
     */
    private void validarHorario(EspacoComum espaco, Instant inicio, Instant fim) {
        LocalTime abertura = espaco.getHorarioAbertura();
        LocalTime fechamento = espaco.getHorarioFechamento();

        if (abertura == null || fechamento == null) {
            return;
        }

        LocalTime horaInicio = inicio.atZone(FUSO).toLocalTime();
        LocalTime horaFim = fim.atZone(FUSO).toLocalTime();

        if (horaInicio.isBefore(abertura) || horaFim.isAfter(fechamento)) {
            throw new IllegalArgumentException(
                    "A reserva deve estar dentro do horário de funcionamento: "
                            + abertura + " às " + fechamento);
        }
    }

    /**
     * Busca reservas de espaços comuns.
     *
     * @param id Identificador único do registro.
     * @return entidade de agendamento espaco localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Agendamento não encontrado: .
     */
    private AgendamentoEspaco buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado: " + id));
    }
}