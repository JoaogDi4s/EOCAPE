package com.eocape.backend.vinculoUnidade;

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

    @Transactional
    public VinculoUnidadeResponse criar(VinculoUnidadeRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado: " + request.usuarioId()));

        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada: " + request.unidadeId()));

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
            // Corrida entre duas requisições: o índice único parcial do banco barra a
            // segunda
            throw new IllegalStateException("O usuário já possui um vínculo principal ativo", e);
        }
    }

    @Transactional(readOnly = true)
    public List<VinculoUnidadeResponse> listar() {
        return vinculoUnidadeRepository.findAll().stream()
                .map(VinculoUnidadeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public VinculoUnidadeResponse buscarPorId(UUID id) {
        VinculoUnidade vinculo = vinculoUnidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + id));
        return VinculoUnidadeResponse.from(vinculo);
    }
}