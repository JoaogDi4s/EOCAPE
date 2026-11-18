package com.eocape.backend.classificado;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.classificado.dto.AlterarStatusClassificadoRequest;
import com.eocape.backend.classificado.dto.AtualizarClassificadoRequest;
import com.eocape.backend.classificado.dto.ClassificadoResponse;
import com.eocape.backend.classificado.dto.CriarClassificadoRequest;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.usuario.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Aplica as regras de negócio e coordena a persistência de classificados.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@Service
@RequiredArgsConstructor
public class ClassificadoService {

    /**
     * Repositório de persistência de classificados.
     */
    private final ClassificadoRepository repository;
    /**
     * Repositório de persistência de condomínios.
     */
    private final CondominioRepository condominioRepository;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;

    /**
     * Cria classificados.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de classificado produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Usuário não encontrado.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     * @throws IllegalArgumentException se ocorrer a condição: Doação não pode ter preço.
     */
    @Transactional
    public ClassificadoResponse criar(UUID solicitanteId, CriarClassificadoRequest request) {
        Condominio condominio = condominioRepository.findById(request.condominioId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Condomínio não encontrado: " + request.condominioId()));

        acesso.exigirVinculo(solicitanteId, condominio.getId());
        validarPreco(request.tipo(), request.preco());

        Usuario usuario = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        Classificado classificado = new Classificado(condominio, usuario, request.titulo(),
                request.descricao(), request.tipo(), request.preco());

        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    /**
     * Gestor vê todos. Morador vê os anúncios ativos do condomínio, mais os
     * próprios
     * em qualquer status.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Usuário sem acesso a este condomínio.
     */
    @Transactional(readOnly = true)
    public List<ClassificadoResponse> listar(UUID solicitanteId, UUID condominioId) {
        acesso.exigirVinculo(solicitanteId, condominioId);

        if (acesso.isGestor(solicitanteId, condominioId)) {
            return repository.findByCondominioIdOrderByCriadoEmDesc(condominioId).stream()
                    .map(ClassificadoResponse::from)
                    .toList();
        }

        List<ClassificadoResponse> ativos = repository
                .findByCondominioIdAndStatusOrderByCriadoEmDesc(condominioId, StatusClassificado.ATIVO)
                .stream()
                .map(ClassificadoResponse::from)
                .toList();

        List<ClassificadoResponse> proprios = repository
                .findByCondominioIdAndUsuarioIdOrderByCriadoEmDesc(condominioId, solicitanteId)
                .stream()
                .map(ClassificadoResponse::from)
                .toList();

        return Stream.concat(ativos.stream(), proprios.stream())
                .distinct()
                .toList();
    }

    /**
     * Busca pelo identificador classificados.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @return dados de classificado produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este anúncio não está disponível.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     */
    @Transactional(readOnly = true)
    public ClassificadoResponse buscarPorId(UUID solicitanteId, UUID id) {
        Classificado classificado = buscar(id);
        UUID condominioId = classificado.getCondominio().getId();

        acesso.exigirVinculo(solicitanteId, condominioId);

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        boolean ativo = classificado.getStatus() == StatusClassificado.ATIVO;

        if (!ehDono && !ativo && !acesso.isGestor(solicitanteId, condominioId)) {
            throw new AccessDeniedException("Este anúncio não está disponível");
        }
        return ClassificadoResponse.from(classificado);
    }

    /**
     * Só o dono edita os dados do anúncio.
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de classificado produzidos pela operação e destinados à resposta da API.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas o dono pode editar este anúncio.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     * @throws IllegalArgumentException se ocorrer a condição: Doação não pode ter preço.
     */
    @Transactional
    public ClassificadoResponse atualizar(UUID solicitanteId, UUID id, AtualizarClassificadoRequest request) {
        Classificado classificado = buscar(id);

        if (!classificado.getUsuario().getId().equals(solicitanteId)) {
            throw new AccessDeniedException("Apenas o dono pode editar este anúncio");
        }

        if (request.titulo() != null) {
            classificado.alterarTitulo(request.titulo());
        }
        if (request.descricao() != null) {
            classificado.alterarDescricao(request.descricao());
        }
        if (request.tipo() != null) {
            TipoClassificado novoTipo = request.tipo();
            // Doação não tem preço: remove automaticamente ao trocar o tipo
            BigDecimal novoPreco = novoTipo == TipoClassificado.DOACAO
                    ? null
                    : (request.preco() != null ? request.preco() : classificado.getPreco());
            validarPreco(novoTipo, novoPreco);
            classificado.alterarTipoEPreco(novoTipo, novoPreco);
        } else if (request.preco() != null) {
            validarPreco(classificado.getTipo(), request.preco());
            classificado.alterarTipoEPreco(classificado.getTipo(), request.preco());
        }

        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    /**
     * O dono ou um gestor muda o status (ex.: encerrar após a venda).
     *
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param id Identificador único do registro.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @return dados de classificado produzidos pela operação e destinados à resposta da API.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Transactional
    public ClassificadoResponse alterarStatus(UUID solicitanteId, UUID id,
            AlterarStatusClassificadoRequest request) {
        Classificado classificado = buscar(id);
        UUID condominioId = classificado.getCondominio().getId();

        boolean ehDono = classificado.getUsuario().getId().equals(solicitanteId);
        if (!ehDono) {
            acesso.exigirGestor(solicitanteId, condominioId);
        }

        classificado.alterarStatus(request.status());
        return ClassificadoResponse.from(repository.saveAndFlush(classificado));
    }

    /**
     * Valida preco antes de prosseguir com a operação.
     *
     * @param tipo Tipo do registro, conforme os valores admitidos pelo domínio.
     * @param preco Preço do anúncio; venda exige valor e doação não permite preço.
     * @throws IllegalArgumentException se ocorrer a condição: Doação não pode ter preço.
     */
    private void validarPreco(TipoClassificado tipo, BigDecimal preco) {
        if (tipo == TipoClassificado.VENDA && preco == null) {
            throw new IllegalArgumentException("Venda exige preço");
        }
        if (tipo == TipoClassificado.DOACAO && preco != null) {
            throw new IllegalArgumentException("Doação não pode ter preço");
        }
    }

    /**
     * Busca classificados.
     *
     * @param id Identificador único do registro.
     * @return entidade de classificado localizada ou persistida pela operação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Anúncio não encontrado: .
     */
    private Classificado buscar(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Anúncio não encontrado: " + id));
    }
}