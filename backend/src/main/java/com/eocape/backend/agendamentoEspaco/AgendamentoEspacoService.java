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

@Service
@RequiredArgsConstructor
public class AgendamentoEspacoService {

    private static final ZoneId FUSO = ZoneId.of("America/Sao_Paulo");

    private final AgendamentoEspacoRepository repository;
    private final EspacoComumRepository espacoRepository;
    private final UsuarioRepository usuarioRepository;
    private final VinculoUnidadeRepository vinculoRepository;
    private final AcessoCondominioService acesso;

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

    /** Morador vê só os próprios; gestor vê todos do condomínio. */
    @Transactional(readOnly = true)
    public List<AgendamentoEspacoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<AgendamentoEspaco> agendamentos = acesso.isGestor(solicitanteId, condominioId)
                ? repository.findByCondominioIdOrderByInicioAsc(condominioId)
                : repository.findByCondominioIdAndUsuarioIdOrderByInicioAsc(condominioId, solicitanteId);

        return agendamentos.stream().map(AgendamentoEspacoResponse::from).toList();
    }

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

    /** Somente gestor confirma uma reserva pendente. */
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

    /** O próprio morador ou um gestor pode cancelar. */
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
     */
    private Unidade buscarUnidadeDoUsuario(UUID usuarioId, UUID condominioId) {
        return vinculoRepository
                .findFirstByUsuarioIdAndAtivoTrueAndUnidadeBlocoCondominioId(usuarioId, condominioId)
                .map(VinculoUnidade::getUnidade)
                .orElseThrow(() -> new IllegalStateException(
                        "É preciso ter uma unidade vinculada no condomínio para reservar espaços"));
    }

    /** Horário de funcionamento do espaço, em horário de Brasília, no mesmo dia. */
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

    private AgendamentoEspaco buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Agendamento não encontrado: " + id));
    }
}