package com.eocape.backend.chamado;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.area.Area;
import com.eocape.backend.area.AreaRepository;
import com.eocape.backend.chamado.dto.ChamadoResponse;
import com.eocape.backend.chamado.dto.CriarChamadoRequest;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChamadoService {

    private final ChamadoRepository chamadoRepository;
    private final UnidadeRepository unidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;
    private final AcessoCondominioService acesso;

    @Transactional
    public ChamadoResponse criar(UUID solicitanteId, CriarChamadoRequest request) {
        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada: " + request.unidadeId()));

        var condominio = unidade.getBloco().getCondominio();
        acesso.exigirPodeAbrirChamado(solicitanteId, condominio.getId(), unidade.getId());

        Area area = null;
        if (request.areaId() != null) {
            area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Área não encontrada: " + request.areaId()));

            if (!area.getCondominio().getId().equals(condominio.getId())) {
                throw new IllegalArgumentException("A área não pertence ao condomínio da unidade");
            }
        }

        Usuario usuario = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Chamado chamado = new Chamado(condominio, usuario, unidade, area,
                request.titulo(), request.descricao(), request.categoria());

        return ChamadoResponse.from(chamadoRepository.saveAndFlush(chamado));
    }

    /** Gestor vê todos do condomínio; morador vê só os próprios. */
    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<Chamado> chamados = acesso.isGestor(solicitanteId, condominioId)
                ? chamadoRepository.findByCondominioIdOrderByCriadoEmDesc(condominioId)
                : chamadoRepository.findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(condominioId, solicitanteId);

        return chamados.stream().map(ChamadoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ChamadoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Chamado chamado = buscar(id);
        UUID condominioId = chamado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = chamado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode visualizar os seus chamados");
        }

        return ChamadoResponse.from(chamado);
    }

    @Transactional
    public ChamadoResponse atualizarStatus(UUID solicitanteId, UUID id, StatusChamado novoStatus) {
        Chamado chamado = buscar(id);
        acesso.exigirGestor(solicitanteId, chamado.getCondominio().getId());

        chamado.atualizarStatus(novoStatus);
        return ChamadoResponse.from(chamadoRepository.saveAndFlush(chamado));
    }

    private Chamado buscar(UUID id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));
    }
}