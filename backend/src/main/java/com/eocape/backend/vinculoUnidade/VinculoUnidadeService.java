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

/**
 * Aplica as regras de negócio e coordena a persistência de vínculos entre usuários e unidades.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class VinculoUnidadeService {

    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculoUnidadeRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidadeRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria vínculos entre usuários e unidades.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de vinculo unidade produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public VinculoUnidadeResponse criar(UUID solicitanteId, VinculoUnidadeRequest request) {
        Usuario usuario = usuarioRepository.findById(request.usuarioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Usuário não encontrado: " + request.usuarioId()));

        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada: " + request.unidadeId()));

        acesso.exigirGestor(solicitanteId, unidade.getBloco().getCondominio().getId());
        if (!java.util.Set.of("PROPRIETARIO", "INQUILINO", "DEPENDENTE").contains(
                request.tipoVinculo() == null ? "" : request.tipoVinculo())) {
            throw new IllegalArgumentException("Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente.");
        }

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
            throw new IllegalStateException("Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador.", e);
        }
    }

    /**
     * Vínculos de um condomínio: somente gestores.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional(readOnly = true)
    public List<VinculoUnidadeResponse> listarDoCondominio(UUID solicitanteId, UUID condominioId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        return vinculoUnidadeRepository.findByUnidadeBlocoCondominioId(condominioId).stream()
                .map(VinculoUnidadeResponse::from)
                .toList();
    }

    /**
     * Vínculos do próprio usuário logado.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    @Transactional(readOnly = true)
    public List<VinculoUnidadeResponse> listarMeus(UUID solicitanteId) {
        return vinculoUnidadeRepository.findByUsuarioId(solicitanteId).stream()
                .map(VinculoUnidadeResponse::from)
                .toList();
    }

    /**
     * O próprio dono do vínculo ou um gestor do condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de vinculo unidade produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
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
