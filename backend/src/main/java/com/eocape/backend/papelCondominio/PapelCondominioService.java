package com.eocape.backend.papelCondominio;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.papelCondominio.dto.PapelCondominioRequest;
import com.eocape.backend.papelCondominio.dto.PapelCondominioResponse;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PapelCondominioService {

    private final PapelCondominioRepository papelRepository;
    private final UsuarioRepository usuarioRepository;
    private final CondominioRepository condominioRepository;
    private final AcessoCondominioService acesso;

    public PapelCondominioService(PapelCondominioRepository papelRepository,
                                  UsuarioRepository usuarioRepository,
                                  CondominioRepository condominioRepository,
                                  AcessoCondominioService acesso) {
        this.papelRepository = papelRepository;
        this.usuarioRepository = usuarioRepository;
        this.condominioRepository = condominioRepository;
        this.acesso = acesso;
    }

    @Transactional
    public PapelCondominioResponse adicionarPapel(UUID solicitanteId, PapelCondominioRequest request) {
        acesso.exigirGestor(solicitanteId, request.condominioId());

        if (papelRepository.existsByUsuarioIdAndCondominioIdAndPapel(
                request.usuarioId(), request.condominioId(), request.papel())) {
            throw new IllegalStateException("Usuário já possui este papel neste condomínio");
        }

        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new IllegalArgumentException("Condomínio não encontrado"));

        PapelCondominio papel = new PapelCondominio(usuario, condominio, request.papel(), request.ativo());
        return PapelCondominioResponse.from(papelRepository.save(papel));
    }

    /** Papéis ativos do próprio usuário logado. */
    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listarMeusPapeis(UUID solicitanteId) {
        return papelRepository.findByUsuarioIdAndAtivoTrue(solicitanteId)
                .stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    /** Todos os papéis de um condomínio: somente gestores. */
    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listarDoCondominio(UUID solicitanteId, UUID condominioId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        return papelRepository.findByCondominioId(condominioId)
                .stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    @Transactional
    public void desativarPapel(UUID solicitanteId, UUID id) {
        PapelCondominio papel = papelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado"));

        acesso.exigirGestor(solicitanteId, papel.getCondominio().getId());
        papel.desativar();
    }
}