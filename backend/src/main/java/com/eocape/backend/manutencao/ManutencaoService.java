package com.eocape.backend.manutencao;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.area.Area;
import com.eocape.backend.area.AreaRepository;
import com.eocape.backend.chamado.Chamado;
import com.eocape.backend.chamado.ChamadoRepository;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.manutencao.dto.CriarManutencaoRequest;
import com.eocape.backend.manutencao.dto.ManutencaoResponse;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de manutenções.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ManutencaoService {

    /**
     * Repositório de persistência de manutenções.
     */
    private final ManutencaoRepository manutencaoRepository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidadeRepository;
    /**
     * Repositório de persistência de áreas.
     */
    private final AreaRepository areaRepository;
    /**
     * Repositório de persistência de chamados de manutenção.
     */
    private final ChamadoRepository chamadoRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /** Somente síndico ou administrador cria manutenção. */
    /**
     * Somente síndico ou administrador cria manutenção.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de manutencao produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws IllegalArgumentException se ocorrer a condição: Informe a unidade ou a área, mas não as duas.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public ManutencaoResponse criar(UUID solicitanteId, CriarManutencaoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirGestor(solicitanteId, condominio.getId());

        Unidade unidade = buscarUnidade(request.unidadeId(), condominio.getId());
        Area area = buscarArea(request.areaId(), condominio.getId());
        Chamado chamado = buscarChamado(request.chamadoId(), condominio.getId());

        if ((unidade == null) == (area == null)) {
            throw new IllegalArgumentException("Informe a unidade ou a área, mas não as duas");
        }

        Usuario criadoPor = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Manutencao manutencao = new Manutencao(condominio, criadoPor, unidade, area, chamado,
                request.titulo(), request.descricao(), request.tipo());

        return ManutencaoResponse.from(manutencaoRepository.saveAndFlush(manutencao));
    }

    /**
     * Qualquer morador ou gestor do condomínio pode acompanhar as manutenções.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ManutencaoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        return manutencaoRepository.findByCondominioId(condominioId).stream()
                .map(ManutencaoResponse::from)
                .toList();
    }

    /**
     * Busca pelo identificador manutenções.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de manutencao produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Manutenção não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public ManutencaoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Manutencao manutencao = manutencaoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + id));

        acesso.exigirVinculo(solicitanteId, manutencao.getCondominio().getId());
        return ManutencaoResponse.from(manutencao);
    }

    /**
     * Localiza unidade exigido pela operação.
     *
     * @param id Identificador único do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return entidade de unidade localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: A unidade não pertence ao condomínio informado.
     */
    private Unidade buscarUnidade(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Unidade unidade = unidadeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Unidade não encontrada: " + id));

        if (!unidade.getBloco().getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("A unidade não pertence ao condomínio informado");
        }
        return unidade;
    }

    /**
     * Localiza area exigido pela operação.
     *
     * @param id Identificador único do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return entidade de area localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Área não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: A área não pertence ao condomínio informado.
     */
    private Area buscarArea(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Area area = areaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Área não encontrada: " + id));

        if (!area.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("A área não pertence ao condomínio informado");
        }
        return area;
    }

    /**
     * Localiza chamado exigido pela operação.
     *
     * @param id Identificador único do registro.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return entidade de chamado localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Chamado não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: O chamado não pertence ao condomínio informado.
     */
    private Chamado buscarChamado(UUID id, UUID condominioId) {
        if (id == null) {
            return null;
        }
        Chamado chamado = chamadoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));

        if (!chamado.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O chamado não pertence ao condomínio informado");
        }
        return chamado;
    }
}