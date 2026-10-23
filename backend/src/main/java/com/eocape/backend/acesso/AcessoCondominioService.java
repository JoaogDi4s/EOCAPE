package com.eocape.backend.acesso;

import com.eocape.backend.papelCondominio.PapelCondominioRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AcessoCondominioService {

    public static final String PAPEL_SINDICO = "SINDICO";
    public static final String PAPEL_ADMINISTRADOR = "ADMINISTRADOR";

    private static final List<String> PAPEIS_GESTORES = List.of(PAPEL_SINDICO, PAPEL_ADMINISTRADOR);

    private final PapelCondominioRepository papelRepository;
    private final VinculoUnidadeRepository vinculoRepository;

    /** Síndico ou administrador ativo no condomínio. */
    @Transactional(readOnly = true)
    public boolean isGestor(UUID usuarioId, UUID condominioId) {
        return papelRepository.existsGestorAtivo(usuarioId, condominioId, PAPEIS_GESTORES);
    }

    /** Morador com vínculo ativo em alguma unidade do condomínio. */
    @Transactional(readOnly = true)
    public boolean isMoradorAtivo(UUID usuarioId, UUID condominioId) {
        return vinculoRepository.existsMoradorAtivo(usuarioId, condominioId);
    }

    /** Leitura: morador ou gestor. */
    @Transactional(readOnly = true)
    public void exigirVinculo(UUID usuarioId, UUID condominioId) {
        if (!isGestor(usuarioId, condominioId) && !isMoradorAtivo(usuarioId, condominioId)) {
            throw new AccessDeniedException("Usuário sem acesso a este condomínio");
        }
    }

    /** Escrita administrativa: somente síndico ou administrador. */
    @Transactional(readOnly = true)
    public void exigirGestor(UUID usuarioId, UUID condominioId) {
        if (!isGestor(usuarioId, condominioId)) {
            throw new AccessDeniedException("Apenas síndico ou administrador pode executar esta ação");
        }
    }

    /** Abrir chamado: gestor em qualquer unidade, morador só na própria unidade ativa. */
    @Transactional(readOnly = true)
    public void exigirPodeAbrirChamado(UUID usuarioId, UUID condominioId, UUID unidadeId) {
        if (isGestor(usuarioId, condominioId)) {
            return;
        }
        if (!vinculoRepository.existsByUsuarioIdAndUnidadeIdAndAtivoTrue(usuarioId, unidadeId)) {
            throw new AccessDeniedException("Você só pode abrir chamados para a sua unidade");
        }
    }
}