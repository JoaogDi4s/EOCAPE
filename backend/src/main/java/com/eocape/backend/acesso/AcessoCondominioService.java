package com.eocape.backend.acesso;

import com.eocape.backend.papelCondominio.PapelCondominioRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Centraliza as verificações de vínculo ativo e de gestão em cada condomínio.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class AcessoCondominioService {

    /**
     * Nome do papel que concede poderes de síndico no condomínio.
     */
    public static final String PAPEL_SINDICO = "SINDICO";
    /**
     * Nome do papel que concede poderes de administração no condomínio.
     */
    public static final String PAPEL_ADMINISTRADOR = "ADMINISTRADOR";

    /**
     * Papéis aceitos nas verificações de permissão de gestão.
     */
    private static final List<String> PAPEIS_GESTORES = List.of(PAPEL_SINDICO, PAPEL_ADMINISTRADOR);

    /**
     * Repositório de persistência de papéis de acesso ao condomínio.
     */
    private final PapelCondominioRepository papelRepository;
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculoRepository;

    /**
     * Síndico ou administrador ativo no condomínio.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean isGestor(UUID usuarioId, UUID condominioId) {
        return papelRepository.existsGestorAtivo(usuarioId, condominioId, PAPEIS_GESTORES);
    }

    /**
     * Morador com vínculo ativo em alguma unidade do condomínio.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean isMoradorAtivo(UUID usuarioId, UUID condominioId) {
        return vinculoRepository.existsMoradorAtivo(usuarioId, condominioId);
    }

    /**
     * Leitura: morador ou gestor.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public void exigirVinculo(UUID usuarioId, UUID condominioId) {
        if (!isGestor(usuarioId, condominioId) && !isMoradorAtivo(usuarioId, condominioId)) {
            throw new AccessDeniedException("Usuário sem acesso a este condomínio");
        }
    }

    /**
     * Escrita administrativa: somente síndico ou administrador.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional(readOnly = true)
    public void exigirGestor(UUID usuarioId, UUID condominioId) {
        if (!isGestor(usuarioId, condominioId)) {
            throw new AccessDeniedException("Apenas síndico ou administrador pode executar esta ação");
        }
    }

    /**
     * Abrir chamado: gestor em qualquer unidade, morador só na própria unidade ativa.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param unidadeId Identificador da unidade habitacional.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode abrir chamados para a sua unidade.
     */
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