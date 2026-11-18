package com.eocape.backend.papelCondominio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Declara as consultas de persistência de papéis de acesso ao condomínio utilizadas pelos serviços da aplicação.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
public interface PapelCondominioRepository extends JpaRepository<PapelCondominio, UUID> {

    /**
     * Consulta os registros pelos critérios: identificador do usuário, situação ativa.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<PapelCondominio> findByUsuarioIdAndAtivoTrue(UUID usuarioId);

    /**
     * Consulta os registros pelos critérios: identificador do condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     */
    List<PapelCondominio> findByCondominioId(UUID condominioId);

    /**
     * Verifica se existem registros pelos critérios: identificador do usuário, identificador do condomínio, papel informado.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param papel Papel de autorização atribuído ao usuário no condomínio.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    boolean existsByUsuarioIdAndCondominioIdAndPapel(UUID usuarioId, UUID condominioId, String papel);

    /**
     * Verifica se existem registros pelos critérios: xists gestor ativo.
     *
     * @param usuarioId Identificador do usuário envolvido na operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param papeis Papéis considerados na consulta de autorização.
     * @return true se a condição verificada for atendida; false caso contrário.
     */
    @Query("""
            select count(p) > 0 from PapelCondominio p
            where p.usuario.id = :usuarioId
              and p.usuario.ativo = true
              and p.condominio.id = :condominioId
              and p.papel in :papeis
              and p.ativo = true
            """)
    boolean existsGestorAtivo(@Param("usuarioId") UUID usuarioId,
                              @Param("condominioId") UUID condominioId,
                              @Param("papeis") Collection<String> papeis);
}