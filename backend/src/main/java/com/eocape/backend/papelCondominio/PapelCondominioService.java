package com.eocape.backend.papelCondominio;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.papelCondominio.dto.PapelCondominioRequest;
import com.eocape.backend.papelCondominio.dto.PapelCondominioResponse;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PapelCondominioService {

    private final PapelCondominioRepository papelRepository;
    private final UsuarioRepository usuarioRepository;
    private final CondominioRepository condominioRepository;

    public PapelCondominioService(PapelCondominioRepository papelRepository,
            UsuarioRepository usuarioRepository,
            CondominioRepository condominioRepository) {
        this.papelRepository = papelRepository;
        this.usuarioRepository = usuarioRepository;
        this.condominioRepository = condominioRepository;
    }

    @Transactional
    public PapelCondominioResponse adicionarPapel(PapelCondominioRequest request) {
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

    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listarPapeisDoUsuario(UUID usuarioId) {
        return papelRepository.findByUsuarioIdAndAtivoTrue(usuarioId)
                .stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listar() {
        return papelRepository.findAll().stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    @Transactional
    public void desativarPapel(UUID id) {
        PapelCondominio papel = papelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado"));
        papel.desativar();
    }
}