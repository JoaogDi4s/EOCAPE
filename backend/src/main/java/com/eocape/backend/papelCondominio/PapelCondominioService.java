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

/**
 * Aplica as regras de negócio e coordena a persistência de papéis de acesso ao condomínio.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
public class PapelCondominioService {

    /**
     * Repositório de persistência de papéis de acesso ao condomínio.
     */
    private final PapelCondominioRepository papelRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Inicializa PapelCondominioService com os dados e as dependências necessários à operação.
     *
     * @param papelRepository Repositório de persistência de papéis de acesso ao condomínio.
     * @param usuarioRepository Repositório de persistência de usuários.
     * @param condominioRepository Repositório de persistência de condomínios.
     * @param acesso Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    public PapelCondominioService(PapelCondominioRepository papelRepository,
                                  UsuarioRepository usuarioRepository,
                                  CondominioRepository condominioRepository,
                                  AcessoCondominioService acesso) {
        this.papelRepository = papelRepository;
        this.usuarioRepository = usuarioRepository;
        this.condominioRepository = condominioRepository;
        this.acesso = acesso;
    }

    /**
     * Executa adicionar papel no fluxo de papéis de acesso ao condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de papel condominio produzidos pela operação e destinados à resposta da API.
     * @throws IllegalStateException se ocorrer a condição: Usuário já possui este papel neste condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: Condomínio não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
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

    /**
     * Papéis ativos do próprio usuário logado.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listarMeusPapeis(UUID solicitanteId) {
        return papelRepository.findByUsuarioIdAndAtivoTrue(solicitanteId)
                .stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    /**
     * Todos os papéis de um condomínio: somente gestores.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional(readOnly = true)
    public List<PapelCondominioResponse> listarDoCondominio(UUID solicitanteId, UUID condominioId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        return papelRepository.findByCondominioId(condominioId)
                .stream()
                .map(PapelCondominioResponse::from)
                .toList();
    }

    /**
     * Executa desativar papel no fluxo de papéis de acesso ao condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @throws IllegalArgumentException se ocorrer a condição: Papel não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public void desativarPapel(UUID solicitanteId, UUID id) {
        PapelCondominio papel = papelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado"));

        acesso.exigirGestor(solicitanteId, papel.getCondominio().getId());
        papel.desativar();
    }
}