package com.eocape.backend.condominio;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.usuario.UsuarioService;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.usuario.dto.request.CadastroUsuarioRequest;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeService;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

/**
 * Operações de moradores sempre limitadas ao condomínio administrado.
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("/condominio/{condominioId}/moradores")
@RequiredArgsConstructor
public class MoradorController {
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso;
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculos;
    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidades;
    /**
     * Serviço responsável por usuários.
     */
    private final UsuarioService usuarios;
    /**
     * Serviço responsável por vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeService vinculoService;
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository;

    /**
     * Representa os dados de unidade morador devolvidos pela aplicação.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param id Identificador único do registro.
     * @param numero Número ou identificação da unidade.
     * @param bloco Bloco relacionado ao registro; em dados de cadastro, número usado na senha inicial.
     * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     * @param ativo Indica se o registro está ativo para uso no sistema.
     */
    public record UnidadeMorador(UUID id, String numero, String bloco, String tipoVinculo, boolean ativo) {}
    /**
     * Representa os dados de morador devolvidos pela aplicação.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param id Identificador único do registro.
     * @param nome Nome usado para identificar o registro.
     * @param email Endereço de e-mail do usuário.
     * @param telefone Número de telefone de contato.
     * @param primeiroAcessoConcluido Indica se o usuário concluiu a ativação e definiu a própria senha.
     * @param unidades Unidades e tipos de vínculo do morador dentro do condomínio consultado.
     */
    public record Morador(UUID id, String nome, String email, String telefone,
                          boolean primeiroAcessoConcluido, List<UnidadeMorador> unidades) {}
    /**
     * Transporta os dados de entrada de cadastro morador, sujeitos às restrições declaradas nos componentes.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param usuario Dados cadastrais do usuário que será criado e vinculado à unidade.
     * @param unidadeId Identificador da unidade habitacional.
     * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     */
    public record CadastroMorador(@NotNull @Valid CadastroUsuarioRequest usuario,
                                  @NotNull UUID unidadeId,
                                  @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE") String tipoVinculo) {
        /**
         * Inicializa CadastroMorador com os dados e as dependências necessários à operação.
         *
         * @param usuario Dados cadastrais do usuário que será criado e vinculado à unidade.
         * @param unidadeId Identificador da unidade habitacional.
         * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
         */
        public CadastroMorador { tipoVinculo = tipoVinculo == null ? "INQUILINO" : tipoVinculo; }
        /**
         * Inicializa CadastroMorador com os dados e as dependências necessários à operação.
         *
         * @param usuario Dados cadastrais do usuário que será criado e vinculado à unidade.
         * @param unidadeId Identificador da unidade habitacional.
         */
        public CadastroMorador(CadastroUsuarioRequest usuario, UUID unidadeId) { this(usuario, unidadeId, "INQUILINO"); }
    }
    /**
     * Transporta os dados de entrada de vincular morador, sujeitos às restrições declaradas nos componentes.
     *
     * @author Equipe EOCAPE
     * @version 0.0.1-SNAPSHOT
     * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
     * @param email Endereço de e-mail do usuário.
     * @param unidadeId Identificador da unidade habitacional.
     * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
     */
    public record VincularMorador(@NotBlank @Pattern(regexp = "\\d{11}") String cpf,
                                  @NotBlank @Email String email, @NotNull UUID unidadeId,
                                  @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE") String tipoVinculo) {
        /**
         * Inicializa VincularMorador com os dados e as dependências necessários à operação.
         *
         * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
         * @param email Endereço de e-mail do usuário.
         * @param unidadeId Identificador da unidade habitacional.
         * @param tipoVinculo Relação do usuário com a unidade: proprietário, inquilino ou dependente.
         */
        public VincularMorador { tipoVinculo = tipoVinculo == null ? "INQUILINO" : tipoVinculo; }
        /**
         * Inicializa VincularMorador com os dados e as dependências necessários à operação.
         *
         * @param cpf CPF do usuário; a camada de serviço normaliza os dígitos antes da consulta.
         * @param email Endereço de e-mail do usuário.
         * @param unidadeId Identificador da unidade habitacional.
         */
        public VincularMorador(String cpf, String email, UUID unidadeId) { this(cpf, email, unidadeId, "INQUILINO"); }
    }

    /**
     * Confere CPF e e-mail de um cadastro ativo e o vincula à unidade sem redefinir a senha ou o vínculo principal anterior.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Este morador já possui vínculo ativo com a unidade selecionada..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @PostMapping("/vincular")
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void vincular(@PathVariable UUID condominioId, @AuthenticationPrincipal UUID solicitanteId,
                          @Valid @RequestBody VincularMorador request) {
        acesso.exigirGestor(solicitanteId, condominioId);
        var unidade = unidades.findById(request.unidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));
        if (!unidade.getBloco().getCondominio().getId().equals(condominioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A unidade pertence a outro condomínio");
        }
        var usuario = usuarioRepository.findByCpf(request.cpf())
                .filter(u -> u.isAtivo() && u.getEmail().equalsIgnoreCase(request.email().trim()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Não foi encontrado um cadastro ativo com esse CPF e e-mail. Confira os dados com o morador."));
        if (vinculos.existsByUsuarioIdAndUnidadeIdAndAtivoTrue(usuario.getId(), unidade.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este morador já possui vínculo ativo com a unidade selecionada.");
        }
        boolean principal = vinculos.findByUsuarioId(usuario.getId()).stream()
                .noneMatch(v -> v.isPrincipal() && v.isAtivo());
        vinculoService.criar(solicitanteId, new VinculoUnidadeRequest(usuario.getId(), unidade.getId(),
                request.tipoVinculo(), principal, true));
    }

    /**
     * Lista os moradores agrupados por usuário, com as unidades e os vínculos pertencentes ao condomínio autorizado.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @return coleção dos registros encontrados pela consulta.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @GetMapping
    @Transactional(readOnly = true)
    public List<Morador> listar(@PathVariable UUID condominioId, @AuthenticationPrincipal UUID solicitanteId) {
        acesso.exigirGestor(solicitanteId, condominioId);
        Map<UUID, Morador> moradores = new LinkedHashMap<>();
        for (var vinculo : vinculos.findByUnidadeBlocoCondominioId(condominioId)) {
            var usuario = vinculo.getUsuario();
            var morador = moradores.computeIfAbsent(usuario.getId(), id -> new Morador(id,
                    usuario.getNome(), usuario.getEmail(), usuario.getTelefone(),
                    usuario.isPrimeiroAcessoConcluido(), new ArrayList<>()));
            var unidade = vinculo.getUnidade();
            morador.unidades().add(new UnidadeMorador(unidade.getId(), unidade.getNumero(),
                    unidade.getBloco().getNome(), vinculo.getTipoVinculo(), vinculo.isAtivo()));
        }
        return moradores.values().stream().sorted(Comparator.comparing(Morador::nome,
                String.CASE_INSENSITIVE_ORDER)).toList();
    }

    /**
     * Cadastra o morador e seu vínculo na mesma transação, após validar a gestão e a unidade do condomínio.
     *
     * @param condominioId Identificador do condomínio ao qual a operação está restrita.
     * @param solicitanteId Identificador do usuário autenticado que solicita a operação.
     * @param request Dados de entrada recebidos e validados para a operação.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: A unidade pertence a outro condomínio.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public void cadastrar(@PathVariable UUID condominioId, @AuthenticationPrincipal UUID solicitanteId,
                           @Valid @RequestBody CadastroMorador request) {
        acesso.exigirGestor(solicitanteId, condominioId);
        var unidade = unidades.findById(request.unidadeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade não encontrada"));
        if (!unidade.getBloco().getCondominio().getId().equals(condominioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A unidade pertence a outro condomínio");
        }
        var usuario = usuarios.cadastrar(request.usuario());
        vinculoService.criar(solicitanteId, new VinculoUnidadeRequest(usuario.id(), unidade.getId(),
                request.tipoVinculo(), true, true));
    }
}
