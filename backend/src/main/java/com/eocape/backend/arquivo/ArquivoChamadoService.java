package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoChamadoResponse;
import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.chamado.ChamadoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoChamadoService {

    private final ArquivoChamadoRepository repository;
    private final ChamadoRepository chamadoRepository;
    private final ArquivoRepository arquivoRepository;
    private final AcessoCondominioService acesso;

    /** O dono do chamado ou um gestor anexa arquivo; o arquivo precisa ser do mesmo condomínio. */
    @Transactional
    public ArquivoChamadoResponse vincular(UUID solicitanteId, UUID chamadoId, UUID arquivoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + chamadoId));
        UUID condominioId = chamado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = chamado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Só o autor do chamado ou um gestor pode anexar arquivos");
        }

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio do chamado");
        }

        if (repository.existsByChamadoIdAndArquivoId(chamadoId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado ao chamado");
        }

        return ArquivoChamadoResponse.from(repository.saveAndFlush(new ArquivoChamado(chamado, arquivo)));
    }

    /** Quem pode ver o chamado pode ver os anexos dele. */
    @Transactional(readOnly = true)
    public List<ArquivoChamadoResponse> listar(UUID solicitanteId, UUID chamadoId) {
        Chamado chamado = chamadoRepository.findById(chamadoId)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + chamadoId));
        UUID condominioId = chamado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = chamado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode ver os anexos dos seus chamados");
        }

        return repository.findByChamadoId(chamadoId).stream()
                .map(ArquivoChamadoResponse::from)
                .toList();
    }

    /** Dono do chamado ou gestor remove o vínculo; o arquivo continua existindo. */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoChamado vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        Chamado chamado = vinculo.getChamado();
        UUID condominioId = chamado.getCondominio().getId();

        boolean ehDono = chamado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, condominioId);
        }

        repository.delete(vinculo);
    }
}