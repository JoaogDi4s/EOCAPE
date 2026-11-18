package com.eocape.backend.arquivo;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.arquivo.dto.ArquivoAtaResponse;
import com.eocape.backend.ata.Ata;
import com.eocape.backend.ata.AtaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Aplica as regras de negócio e coordena a persistência de anexos de atas.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ArquivoAtaService {

    /**
     * Repositório de persistência de anexos de atas.
     */
    private final ArquivoAtaRepository repository;
    /**
     * Repositório de persistência de atas de assembleia.
     */
    private final AtaRepository ataRepository;
    /**
     * Repositório de persistência de arquivos.
     */
    private final ArquivoRepository arquivoRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Só gestor vincula arquivo a uma ata; o arquivo precisa ser do mesmo condomínio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param ataId Identificador da ata de assembleia.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return dados de arquivo ata produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: O arquivo não pertence ao condomínio da ata.
     * @throws IllegalStateException se ocorrer a condição: Este arquivo já está vinculado à ata.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public ArquivoAtaResponse vincular(UUID solicitanteId, UUID ataId, UUID arquivoId) {
        Ata ata = ataRepository.findById(ataId)
                .orElseThrow(() -> new EntityNotFoundException("Ata não encontrada: " + ataId));
        UUID condominioId = ata.getCondominio().getId();

        acesso.exigirGestor(solicitanteId, condominioId);

        Arquivo arquivo = arquivoRepository.findById(arquivoId)
                .orElseThrow(() -> new EntityNotFoundException("Arquivo não encontrado: " + arquivoId));

        if (!arquivo.getCondominio().getId().equals(condominioId)) {
            throw new IllegalArgumentException("O arquivo não pertence ao condomínio da ata");
        }

        if (repository.existsByAtaIdAndArquivoId(ataId, arquivoId)) {
            throw new IllegalStateException("Este arquivo já está vinculado à ata");
        }

        return ArquivoAtaResponse.from(repository.saveAndFlush(new ArquivoAta(ata, arquivo)));
    }

    /**
     * Lista os anexos da ata. Atas em rascunho só aparecem para gestor,
     * e o morador só vê anexos de atas publicadas.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param ataId Identificador da ata de assembleia.
     * @return coleção dos registros encontrados pela consulta.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Ata não encontrada: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ArquivoAtaResponse> listar(UUID solicitanteId, UUID ataId) {
        Ata ata = ataRepository.findById(ataId)
                .orElseThrow(() -> new EntityNotFoundException("Ata não encontrada: " + ataId));
        UUID condominioId = ata.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean gestor = acesso.isGestor(solicitanteId, condominioId);
        if (!ata.isPublicada() && !gestor) {
            throw new EntityNotFoundException("Ata não encontrada: " + ataId);
        }

        return repository.findByAtaId(ataId).stream()
                .map(ArquivoAtaResponse::from)
                .toList();
    }

    /**
     * Gestor remove o vínculo (o arquivo continua existindo).
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param vinculoId Identificador do vínculo do anexo com o registro.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Vínculo não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public void desvincular(UUID solicitanteId, UUID vinculoId) {
        ArquivoAta vinculo = repository.findById(vinculoId)
                .orElseThrow(() -> new EntityNotFoundException("Vínculo não encontrado: " + vinculoId));

        acesso.exigirGestor(solicitanteId, vinculo.getAta().getCondominio().getId());

        repository.delete(vinculo);
    }
}