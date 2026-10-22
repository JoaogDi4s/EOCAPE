package com.eocape.backend.aviso;

import com.eocape.backend.aviso.dto.request.AvisoAtualizacaoRequest;
import com.eocape.backend.aviso.dto.request.AvisoRequest;
import com.eocape.backend.aviso.dto.response.AvisoResponse;
import com.eocape.backend.bloco.Bloco;
import com.eocape.backend.bloco.BlocoRepository;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvisoService {

    private final AvisoRepository avisoRepository;
    private final CondominioRepository condominioRepository;
    private final UsuarioRepository usuarioRepository;
    private final BlocoRepository blocoRepository;

    @Transactional
    public AvisoResponse criar(UUID condominioId, UUID autorId, AvisoRequest request) {
        Condominio condominio = condominioRepository.findById(condominioId)
                .orElseThrow(() -> naoEncontrado("Condomínio não encontrado"));

        Usuario autor = usuarioRepository.findById(autorId)
                .orElseThrow(() -> naoEncontrado("Usuário não encontrado"));

        Bloco bloco = blocoRepository.findById(request.blocoId())
                .filter(b -> b.getCondominio().getId().equals(condominioId))
                .orElseThrow(() -> naoEncontrado("Bloco não encontrado neste condomínio"));

        Aviso aviso = new Aviso(condominio, autor, bloco,
                request.titulo(), request.conteudo(), request.importante());

        return AvisoResponse.from(avisoRepository.save(aviso));
    }

    public Page<AvisoResponse> listar(UUID condominioId, Pageable pageable) {
        return avisoRepository.findByCondominioId(condominioId, pageable)
                .map(AvisoResponse::from);
    }

    public AvisoResponse buscar(UUID condominioId, UUID avisoId) {
        return AvisoResponse.from(buscarEntidade(condominioId, avisoId));
    }

    @Transactional
    public AvisoResponse atualizar(UUID condominioId, UUID avisoId, AvisoAtualizacaoRequest request) {
        Aviso aviso = buscarEntidade(condominioId, avisoId);
        aviso.atualizar(request.titulo(), request.conteudo(), request.importante());
        return AvisoResponse.from(aviso);
    }

    @Transactional
    public void remover(UUID condominioId, UUID avisoId) {
        avisoRepository.delete(buscarEntidade(condominioId, avisoId));
    }

    private Aviso buscarEntidade(UUID condominioId, UUID avisoId) {
        return avisoRepository.findByIdAndCondominioId(avisoId, condominioId)
                .orElseThrow(() -> naoEncontrado("Aviso não encontrado"));
    }

    private ResponseStatusException naoEncontrado(String mensagem) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, mensagem);
    }
}