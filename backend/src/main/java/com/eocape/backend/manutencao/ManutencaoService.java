package com.eocape.backend.manutencao;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.area.Area;
import com.eocape.backend.area.AreaRepository;
import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.chamado.ChamadoRepository;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.manutencao.dto.CriarManutencaoRequest;
import com.eocape.backend.manutencao.dto.ManutencaoResponse;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ManutencaoService {

    private final ManutencaoRepository manutencaoRepository;
    private final CondominioRepository condominioRepository;
    private final UnidadeRepository unidadeRepository;
    private final AreaRepository areaRepository;
    private final ChamadoRepository chamadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcessoCondominioService acesso;

    /** Somente síndico ou administrador cria manutenção. */
    /** Somente síndico ou administrador cria manutenção. */
    @Transactional
    public ManutencaoResponse criar(UUID solicitanteId, CriarManutencaoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirGestor(solicitanteId, condominio.getId());

        Unidade unidade = buscarUnidade(request.unidadeId(), condominio.getId());
        Area area = buscarArea(request.areaId(), condominio.getId());
        Chamado chamado = buscarChamado(request.chamadoId(), condominio.getId());

        if ((unidade == null) == (area == null)) {
            throw new IllegalArgumentException("Informe a unidade ou a área, mas não as duas");
        }

        Usuario criadoPor = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Manutencao manutencao = new Manutencao(condominio, criadoPor, unidade, area, chamado,
                request.titulo(), request.descricao(), request.tipo());

        return ManutencaoResponse.from(manutencaoRepository.saveAndFlush(manutencao));
    }

    /** Qualquer morador ou gestor do condomínio pode acompanhar as manutenções. */
    @Transactional(readOnly = true)
    public List<ManutencaoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        return manutencaoRepository.findByCondominioId(condominioId).stream()
                .map(ManutencaoResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ManutencaoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Manutencao manutencao = manutencaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + id));

        acesso.exigirVinculo(solicitanteId, manutencao.getCondominio().getId());
        return ManutencaoResponse.from(manutencao);
    }

    private Unidade buscarUnidade(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada: " + id));

        if (!unidade.getBloco().getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("A unidade não pertence ao condomínio informado");
        }
        return unidade;
    }

    private Area buscarArea(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área não encontrada: " + id));

        if (!area.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("A área não pertence ao condomínio informado");
        }
        return area;
    }

    private Chamado buscarChamado(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));

        if (!chamado.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O chamado não pertence ao condomínio informado");
        }
        return chamado;
    }
}