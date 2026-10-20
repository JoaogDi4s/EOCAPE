package com.eocape.backend.vinculoUnidade;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VinculoUnidadeService {

    private final VinculoUnidadeRepository vinculoUnidadeRepository;
    private final UsuarioRepository usuarioRepository;
    private final UnidadeRepository unidadeRepository;
    private final AcessoCondominioService acesso;

    @Transactional
    public VinculoUnidadeResponse criar(UUID solicitanteId, VinculoUnidadeRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado: " + request.usuarioId()));

        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada: " + request.unidadeId()));

        acesso.exigirGestor(solicitanteId, unidade.getBloco().getCondominio().getId());

        if (request.principal() && request.ativo()) {
            boolean jaTemPrincipalAtivo = vinculoUnidadeRepository
                    .findByUsuarioId(request.usuarioId())
                    .stream()
                    .anyMatch(v -> v.isPrincipal() && v.isAtivo());

            if (jaTemPrincipalAtivo) {
                throw new IllegalStateException("O usuário já possui um vínculo principal ativo");
            }
        }

        try {
            VinculoUnidade vinculo = vinculoUnidadeRepository.saveAndFlush(
                    new VinculoUnidade(usuario, unidade, request.tipoVinculo(),
                            request.principal(), request.ativo(), null));
            return VinculoUnidadeResponse.from(vinculo);
        } catch (DataIntegrityViolationException e) {
            // Corrida entre duas requisições: o índice único parcial do banco barra a segunda
            throw new IllegalStateException("O usuário já possui um vínculo principal ativo", e);
        }
    }

    /** Vínculos de um condomínio: somente gestores. */
    @Transactional(readOnly = true)
    public List<VinculoUnidadeResponse> listarDoCondominio(UUID solicitanteId, UUID condominioId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        return vinculoUnidadeRepository.findByUnidadeBlocoCondominioId(condominioId).stream()
                .map(VinculoUnidadeResponse::from)
                .toList();
    }

    /** Vínculos do próprio usuário logado. */
    @Transactional(readOnly = true)
    public List<VinculoUnidadeResponse> listarMeus(UUID solicitanteId) {
        return vinculoUnidadeRepository.findByUsuarioId(solicitanteId).stream()
                .map(VinculoUnidadeResponse::from)
                .toList();
    }

    /** O próprio dono do vínculo ou um gestor do condomínio. */
    @Transactional(readOnly = true)
    public VinculoUnidadeResponse buscarPorId(UUID solicitanteId, UUID id) {
        VinculoUnidade vinculo = vinculoUnidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + id));

        boolean ehDono = vinculo.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, vinculo.getUnidade().getBloco().getCondominio().getId());
        }
        return VinculoUnidadeResponse.from(vinculo);
    }
}