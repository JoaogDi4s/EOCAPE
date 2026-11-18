package com.eocape.backend.chamado;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.area.Area;
import com.eocape.backend.area.AreaRepository;
import com.eocape.backend.chamado.dto.ChamadoResponse;
import com.eocape.backend.chamado.dto.CriarChamadoRequest;
import com.eocape.backend.chamado.dto.HistoricoChamadoResponse;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de chamados de manutenção.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ChamadoService {

    /**
     * Repositório de persistência de chamados de manutenção.
     */
    private final ChamadoRepository chamadoRepository;
    /**
     * Repositório de persistência de histórico dos chamados.
     */
    private final HistoricoChamadoRepository historicoRepository;
    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidadeRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Repositório de persistência de áreas.
     */
    private final AreaRepository areaRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria chamados de manutenção.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de chamado produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Área não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: A área não pertence ao condomínio da unidade.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode abrir chamados para a sua unidade.
     */
    @Transactional
    public ChamadoResponse criar(UUID solicitanteId, CriarChamadoRequest request) {
        Unidade unidade = unidadeRepository.findById(request.unidadeId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Unidade não encontrada: " + request.unidadeId()));

        var condominio = unidade.getBloco().getCondominio();
        acesso.exigirPodeAbrirChamado(solicitanteId, condominio.getId(), unidade.getId());

        Area area = null;
        if (request.areaId() != null) {
            area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Área não encontrada: " + request.areaId()));

            if (!area.getCondominio().getId().equals(condominio.getId())) {
                throw new IllegalArgumentException("A área não pertence ao condomínio da unidade");
            }
        }

        Usuario usuario = buscarUsuario(solicitanteId);

        Chamado chamado = new Chamado(condominio, usuario, unidade, area,
                request.titulo(), request.descricao(), request.categoria());
        Chamado salvo = chamadoRepository.saveAndFlush(chamado);

        historicoRepository.save(new HistoricoChamado(
                salvo, usuario, null, StatusChamado.ABERTO, "Chamado aberto"));

        return ChamadoResponse.from(salvo);
    }

    /**
     * Gestor vê todos do condomínio; morador vê só os próprios.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ChamadoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        List<Chamado> chamados = acesso.isGestor(solicitanteId, condominioId)
                ? chamadoRepository.findByCondominioIdOrderByCriadoEmDesc(condominioId)
                : chamadoRepository.findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(condominioId, solicitanteId);

        return chamados.stream().map(ChamadoResponse::from).toList();
    }

    /**
     * Busca pelo identificador chamados de manutenção.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de chamado produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus chamados.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     */
    @Transactional(readOnly = true)
    public ChamadoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Chamado chamado = buscar(id);
        UUID condominioId = chamado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = chamado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Você só pode visualizar os seus chamados");
        }

        return ChamadoResponse.from(chamado);
    }

    /**
     * Atualiza o status e o histórico de chamados de manutenção.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @param novoStatus Estado que será aplicado ao registro.
     * @param comentario Comentário que contextualiza a alteração de status.
     * @return dados de chamado produzidos pela operação e destinados à resposta da API.
     * @throws IllegalStateException se ocorrer a condição: O chamado já está com este status.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public ChamadoResponse atualizarStatus(UUID solicitanteId, UUID id,
                                           StatusChamado novoStatus, String comentario) {
        Chamado chamado = buscar(id);
        acesso.exigirGestor(solicitanteId, chamado.getCondominio().getId());

        StatusChamado anterior = chamado.getStatus();
        if (anterior == novoStatus) {
            throw new IllegalStateException("O chamado já está com este status");
        }

        chamado.atualizarStatus(novoStatus);
        Chamado salvo = chamadoRepository.saveAndFlush(chamado);

        Usuario autor = buscarUsuario(solicitanteId);
        historicoRepository.save(new HistoricoChamado(salvo, autor, anterior, novoStatus, comentario));

        return ChamadoResponse.from(salvo);
    }

    /**
     * Histórico de status: mesma regra de acesso de buscarPorId.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param chamadoId Identificador do chamado.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Você só pode visualizar os seus chamados.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     */
    @Transactional(readOnly = true)
    public List<HistoricoChamadoResponse> historico(UUID solicitanteId, UUID chamadoId) {
        buscarPorId(solicitanteId, chamadoId);

        return historicoRepository.findByChamadoIdOrderByCriadoEmAsc(chamadoId).stream()
                .map(HistoricoChamadoResponse::from)
                .toList();
    }

    /**
     * Busca chamados de manutenção.
     *
     * @param id Identificador único do registro.
     * @return entidade de chamado localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     */
    private Chamado buscar(UUID id) {
        return chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));
    }

    /**
     * Localiza usuario exigido pela operação.
     *
     * @param id Identificador único do registro.
     * @return entidade de usuario localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     */
    private Usuario buscarUsuario(UUID id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }
}