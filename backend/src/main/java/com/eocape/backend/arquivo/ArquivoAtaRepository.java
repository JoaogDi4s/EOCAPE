package com.eocape.backend.arquivo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de anexos de atas utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface ArquivoAtaRepository extends JpaRepository<ArquivoAta, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador da ata.
     *
     * @param ataId Identificador da ata de assembleia.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<ArquivoAta> findByAtaId(UUID ataId);

    /**
     * Verifica se existem registros pelos critérios: identificador da ata, identificador do arquivo.
     *
     * @param ataId Identificador da ata de assembleia.
     * @param arquivoId Identificador do arquivo armazenado.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByAtaIdAndArquivoId(UUID ataId, UUID arquivoId);
    /**
     * Verifica se existem registros pelos critérios: identificador do arquivo, publicação da ata, condomínio da ata.
     *
     * @param arquivoId Identificador do arquivo armazenado.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByArquivoIdAndAtaPublicadaTrueAndAtaCondominioId(UUID arquivoId, UUID condominioId);
}
