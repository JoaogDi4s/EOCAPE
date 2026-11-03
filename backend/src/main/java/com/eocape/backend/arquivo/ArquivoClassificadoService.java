package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoClassificadoResponse;
import com.eocape.backend.classificado.Classificado;
import com.eocape.backend.classificado.ClassificadoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoClassificadoService {

    private final ArquivoClassificadoRepository repository;
    private final ClassificadoRepository classificadoRepository;
    private final ArquivoRepository arquivoRepository;
    private final AcessoCondominioService acesso;

    /** Dono do anúncio ou gestor anexa arquivo; o arquivo precisa ser do mesmo condomínio. */
    @Transactional
    public ArquivoClassificadoResponse vincular(UUID solicitanteId, UUID classificadoId, UUID arquivoId) {
        Classificado classificado = classificadoRepository.findById(classificadoId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado: " + classificadoId));
        UUID condominioId = classificado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Só o dono do anúncio ou um gestor pode anexar arquivos");
        }

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio do anúncio");
        }

        if (repository.existsByClassificadoIdAndArquivoId(classificadoId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado ao anúncio");
        }

        return ArquivoClassificadoResponse.from(
                repository.saveAndFlush(new ArquivoClassificado(classificado, arquivo)));
    }

    /**
     * Quem pode ver o anúncio pode ver os anexos. Anúncio encerrado só aparece para o dono
     * e para gestor, da mesma forma que o próprio anúncio.
     */
    @Transactional(readOnly = true)
    public List<ArquivoClassificadoResponse> listar(UUID solicitanteId, UUID classificadoId) {
        Classificado classificado = classificadoRepository.findById(classificadoId)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado: " + classificadoId));
        UUID condominioId = classificado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        boolean gestor = acesso.isGestor(solicitanteId, condominioId);
        boolean ativo = classificado.getStatus().name().equals("ATIVO");

        if (!ehDono && !gestor) {
            if (!ativo) {
                throw new AccessDeniedException("Este anúncio não está disponível");
            }
        }

        return repository.findByClassificadoId(classificadoId).stream()
                .map(ArquivoClassificadoResponse::from)
                .toList();
    }

    /** Dono do anúncio ou gestor remove o vínculo; o arquivo continua existindo. */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoClassificado vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        Classificado classificado = vinculo.getClassificado();
        UUID condominioId = classificado.getCondominio().getId();

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, condominioId);
        }

        repository.delete(vinculo);
    }
}