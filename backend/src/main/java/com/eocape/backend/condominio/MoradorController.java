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

/** Operações de moradores sempre limitadas ao condomínio administrado. */
@RestController
@RequestMapping("/condominio/{condominioId}/moradores")
@RequiredArgsConstructor
public class MoradorController {
    private final AcessoCondominioService acesso;
    private final VinculoUnidadeRepository vinculos;
    private final UnidadeRepository unidades;
    private final UsuarioService usuarios;
    private final VinculoUnidadeService vinculoService;
    private final UsuarioRepository usuarioRepository;

    public record UnidadeMorador(UUID id, String numero, String bloco, String tipoVinculo, boolean ativo) {}
    public record Morador(UUID id, String nome, String email, String telefone,
                          boolean primeiroAcessoConcluido, List<UnidadeMorador> unidades) {}
    public record CadastroMorador(@NotNull @Valid CadastroUsuarioRequest usuario,
                                  @NotNull UUID unidadeId,
                                  @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE") String tipoVinculo) {
        public CadastroMorador { tipoVinculo = tipoVinculo == null ? "INQUILINO" : tipoVinculo; }
        public CadastroMorador(CadastroUsuarioRequest usuario, UUID unidadeId) { this(usuario, unidadeId, "INQUILINO"); }
    }
    public record VincularMorador(@NotBlank @Pattern(regexp = "\\d{11}") String cpf,
                                  @NotBlank @Email String email, @NotNull UUID unidadeId,
                                  @Pattern(regexp = "PROPRIETARIO|INQUILINO|DEPENDENTE") String tipoVinculo) {
        public VincularMorador { tipoVinculo = tipoVinculo == null ? "INQUILINO" : tipoVinculo; }
        public VincularMorador(String cpf, String email, UUID unidadeId) { this(cpf, email, unidadeId, "INQUILINO"); }
    }

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
