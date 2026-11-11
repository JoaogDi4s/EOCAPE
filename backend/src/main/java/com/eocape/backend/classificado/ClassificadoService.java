package com.eocape.backend.classificado;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.classificado.dto.AlterarStatusClassificadoRequest;
import com.eocape.backend.classificado.dto.AtualizarClassificadoRequest;
import com.eocape.backend.classificado.dto.ClassificadoResponse;
import com.eocape.backend.classificado.dto.CriarClassificadoRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ClassificadoService {

    private final ClassificadoRepository repository;
    private final CondominioRepository condominioRepository;
    private final UsuarioRepository usuarioRepository;
    private final AcessoCondominioService acesso;

    @Transactional
    public ClassificadoResponse criar(UUID solicitanteId, CriarClassificadoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirVinculo(solicitanteId, condominio.getId());
        validarPreco(request.tipo(), request.preco());

        Usuario usuario = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Classificado classificado = new Classificado(condominio, usuario, request.titulo(),
                request.descricao(), request.tipo(), request.preco());

        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    /**
     * Gestor vê todos. Morador vê os anúncios ativos do condomínio, mais os
     * próprios
     * em qualquer status.
     */
    @Transactional(readOnly = true)
    public List<ClassificadoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        if (acesso.isGestor(solicitanteId, condominioId)) {
            return repository.findByCondominioIdOrderByCriadoEmDesc(condominioId).stream()
                    .map(ClassificadoResponse::from)
                    .toList();
        }

        List<ClassificadoResponse> ativos = repository
                .findByCondominioIdAndStatusOrderByCriadoEmDesc(condominioId, StatusClassificado.ATIVO)
                .stream()
                .map(ClassificadoResponse::from)
                .toList();

        List<ClassificadoResponse> proprios = repository
                .findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(condominioId, solicitanteId)
                .stream()
                .map(ClassificadoResponse::from)
                .toList();

        return Stream.concat(ativos.stream(), proprios.stream())
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassificadoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Classificado classificado = buscar(id);
        UUID condominioId = classificado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        boolean ativo = classificado.getStatus() == StatusClassificado.ATIVO;

        if (!ehDono && !ativo && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Este anúncio não está disponível");
        }
        return ClassificadoResponse.from(classificado);
    }

    /** Só o dono edita os dados do anúncio. */
    @Transactional
    public ClassificadoResponse atualizar(UUID solicitanteId, UUID id, AtualizarClassificadoRequest request) {
        Classificado classificado = buscar(id);

        if (!classificado.getUsuario().getId().equals(solicitanteId)) {
            throw new AccessDeniedException("Apenas o dono pode editar este anúncio");
        }

        if (request.titulo() != null) {
            classificado.alterarTitulo(request.titulo());
        }
        if (request.descricao() != null) {
            classificado.alterarDescricao(request.descricao());
        }
        if (request.tipo() != null) {
            TipoClassificado novoTipo = request.tipo();
            // Doação não tem preço: remove automaticamente ao trocar o tipo
            BigDecimal novoPreco = novoTipo == TipoClassificado.DOACAO
                    ? null
                    : (request.preco() != null ? request.preco() : classificado.getPreco());
            validarPreco(novoTipo, novoPreco);
            classificado.alterarTipoEPreco(novoTipo, novoPreco);
        } else if (request.preco() != null) {
            validarPreco(classificado.getTipo(), request.preco());
            classificado.alterarTipoEPreco(classificado.getTipo(), request.preco());
        }

        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    /** O dono ou um gestor muda o status (ex.: encerrar após a venda). */
    @Transactional
    public ClassificadoResponse alterarStatus(UUID solicitanteId, UUID id,
            AlterarStatusClassificadoRequest request) {
        Classificado classificado = buscar(id);
        UUID condominioId = classificado.getCondominio().getId();

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, condominioId);
        }

        classificado.alterarStatus(request.status());
        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    private void validarPreco(TipoClassificado tipo, BigDecimal preco) {
        if (tipo == TipoClassificado.VENDA && preco == null) {
            throw new IllegalArgumentException("Venda exige preço");
        }
        if (tipo == TipoClassificado.DOACAO && preco != null) {
            throw new IllegalArgumentException("Doação não pode ter preço");
        }
    }

    private Classificado buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado: " + id));
    }
}