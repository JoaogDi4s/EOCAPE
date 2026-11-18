package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoManutencaoResponse;
import com.eocape.backend.manutencao.Manutencao;
import com.eocape.backend.manutencao.ManutencaoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de anexos de manutenções.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ArquivoManutencaoService {

    /**
     * Repositório de persistência de anexos de manutenções.
     */
    private final ArquivoManutencaoRepository repository;
    /**
     * Repositório de persistência de manutenções.
     */
    private final ManutencaoRepository manutencaoRepository;
    /**
     * Repositório de persistência de arquivos.
     */
    private final ArquivoRepository arquivoRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Só gestor anexa arquivo a manutenção; o arquivo precisa ser do mesmo condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param manutencaoId Identificador da manutenção.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return dados de arquivo manutencao produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio da manutenção.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado à manutenção.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public ArquivoManutencaoResponse vincular(UUID solicitanteId, UUID manutencaoId, UUID arquivoId) {
        Manutencao manutencao = manutencaoRepository.findById(manutencaoId)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + manutencaoId));
        UUID condominioId = manutencao.getCondominio().getId();

        acesso.exigirGestor(solicitanteId, condominioId);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio da manutenção");
        }

        if (repository.existsByManutencaoIdAndArquivoId(manutencaoId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado à manutenção");
        }

        return ArquivoManutencaoResponse.from(
                repository.saveAndFlush(new ArquivoManutencao(manutencao, arquivo)));
    }

    /**
     * Qualquer morador ou gestor do condomínio pode ver os anexos de manutenção.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param manutencaoId Identificador da manutenção.
     * @return coleção dos registros encontrados pela consulta.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Manutenção não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ArquivoManutencaoResponse> listar(UUID solicitanteId, UUID manutencaoId) {
        Manutencao manutencao = manutencaoRepository.findById(manutencaoId)
                .orElseThrow(() -> new EntityNotFoundException("Manutenção não encontrada: " + manutencaoId));

        acesso.exigirVinculo(solicitanteId, manutencao.getCondominio().getId());

        return repository.findByManutencaoId(manutencaoId).stream()
                .map(ArquivoManutencaoResponse::from)
                .toList();
    }

    /**
     * Só gestor remove o vínculo; o arquivo continua existindo.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoManutencao vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        acesso.exigirGestor(solicitanteId, vinculo.getManutencao().getCondominio().getId());

        repository.delete(vinculo);
    }
}