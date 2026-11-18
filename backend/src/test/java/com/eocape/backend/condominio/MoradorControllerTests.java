package com.eocape.backend.condominio;

import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.unidade.Unidade;
import com.eocape.backend.unidade.UnidadeRepository;
import com.eocape.backend.usuario.UsuarioService;
import com.eocape.backend.usuario.UsuarioRepository;
import com.eocape.backend.usuario.Usuario;
import com.eocape.backend.vinculoUnidade.VinculoUnidade;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import org.mockito.ArgumentCaptor;
import com.eocape.backend.usuario.dto.request.CadastroUsuarioRequest;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeRepository;
import com.eocape.backend.vinculoUnidade.VinculoUnidadeService;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Verifica os cenários de morador controller e as regras exercitadas pelos testes desta classe.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
class MoradorControllerTests {
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    private final AcessoCondominioService acesso = mock(AcessoCondominioService.class);
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeRepository vinculos = mock(VinculoUnidadeRepository.class);
    /**
     * Repositório de persistência de unidades.
     */
    private final UnidadeRepository unidades = mock(UnidadeRepository.class);
    /**
     * Serviço responsável por usuários.
     */
    private final UsuarioService usuarios = mock(UsuarioService.class);
    /**
     * Serviço responsável por vínculos entre usuários e unidades.
     */
    private final VinculoUnidadeService vinculoService = mock(VinculoUnidadeService.class);
    /**
     * Repositório de persistência de usuários.
     */
    private final UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
    /**
     * Controller de moradores exercitado pelos testes.
     */
    private final MoradorController controller = new MoradorController(acesso, vinculos, unidades, usuarios, vinculoService, usuarioRepository);
    /**
     * Identificador de user.
     */
    /**
     * Identificador de condo.
     */
    private final UUID condo = UUID.randomUUID(), user = UUID.randomUUID();
    /**
     * Verifica que rejeita email divergente evinculo duplicado sem alterar cadastro.
     *
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Este morador já possui vínculo ativo com a unidade selecionada..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @Test void rejeitaEmailDivergenteEVinculoDuplicadoSemAlterarCadastro() {
        UUID unitId = UUID.randomUUID(), residentId = UUID.randomUUID();
        var unidade = mock(Unidade.class, RETURNS_DEEP_STUBS);
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(unidade.getBloco().getCondominio().getId()).thenReturn(condo);
        when(unidade.getId()).thenReturn(unitId);
        var usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(residentId); when(usuario.isAtivo()).thenReturn(true);
        when(usuario.getEmail()).thenReturn("correto@example.test");
        when(usuarioRepository.findByCpf("00000000000")).thenReturn(Optional.of(usuario));
        var mismatch = assertThrows(ResponseStatusException.class, () -> controller.vincular(condo,user,
                new MoradorController.VincularMorador("00000000000","outro@example.test",unitId)));
        assertEquals(400,mismatch.getStatusCode().value());
        when(vinculos.existsByUsuarioIdAndUnidadeIdAndAtivoTrue(residentId,unitId)).thenReturn(true);
        var duplicate = assertThrows(ResponseStatusException.class, () -> controller.vincular(condo,user,
                new MoradorController.VincularMorador("00000000000","correto@example.test",unitId)));
        assertEquals(409,duplicate.getStatusCode().value());
        verifyNoInteractions(usuarios,vinculoService);
    }
    /**
     * Verifica que vincula cadastro existente sem redefinir dados ou vinculo principal.
     *
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Este morador já possui vínculo ativo com a unidade selecionada..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @Test void vinculaCadastroExistenteSemRedefinirDadosOuVinculoPrincipal() {
        UUID unitId = UUID.randomUUID(), residentId = UUID.randomUUID();
        var unidade = mock(Unidade.class, RETURNS_DEEP_STUBS);
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(unidade.getBloco().getCondominio().getId()).thenReturn(condo);
        when(unidade.getId()).thenReturn(unitId);
        var usuario = mock(Usuario.class);
        when(usuario.getId()).thenReturn(residentId);
        when(usuario.getEmail()).thenReturn("pessoa@example.test"); when(usuario.isAtivo()).thenReturn(true);
        when(usuarioRepository.findByCpf("00000000000")).thenReturn(Optional.of(usuario));
        var principal = mock(VinculoUnidade.class);
        when(principal.isAtivo()).thenReturn(true); when(principal.isPrincipal()).thenReturn(true);
        when(vinculos.findByUsuarioId(residentId)).thenReturn(List.of(principal));
        controller.vincular(condo,user,new MoradorController.VincularMorador("00000000000","PESSOA@example.test",unitId));
        var captor = ArgumentCaptor.forClass(VinculoUnidadeRequest.class);
        verify(vinculoService).criar(eq(user),captor.capture());
        assertFalse(captor.getValue().principal()); assertEquals(residentId,captor.getValue().usuarioId());
        assertEquals("INQUILINO",captor.getValue().tipoVinculo());
        verifyNoInteractions(usuarios); verify(usuario,never()).alterarSenha(anyString());
    }
    /**
     * Verifica que nega vinculo existente sem papel antes de consultar identidade.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Negado.
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: Este morador já possui vínculo ativo com a unidade selecionada..
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @Test void negaVinculoExistenteSemPapelAntesDeConsultarIdentidade() {
        doThrow(new AccessDeniedException("Negado")).when(acesso).exigirGestor(user,condo);
        assertThrows(AccessDeniedException.class, () -> controller.vincular(condo,user,new MoradorController.VincularMorador("00000000000","pessoa@example.test",UUID.randomUUID())));
        verifyNoInteractions(usuarioRepository, unidades, vinculoService);
    }
    /**
     * Verifica que nega consulta sem papel de gestor antes de buscar dados.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Negado.
     */
    @Test void negaConsultaSemPapelDeGestorAntesDeBuscarDados() {
        doThrow(new AccessDeniedException("Negado")).when(acesso).exigirGestor(user, condo);
        assertThrows(AccessDeniedException.class, () -> controller.listar(condo, user));
        verifyNoInteractions(vinculos);
    }
    /**
     * Verifica que cadastro novo usa tipo aceito pelo banco.
     *
     * @throws org.springframework.web.server.ResponseStatusException se a regra de negócio ou o estado atual não permitir a operação.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @Test void cadastroNovoUsaTipoAceitoPeloBanco() {
        UUID unitId = UUID.randomUUID(), residentId = UUID.randomUUID();
        var unidade = mock(Unidade.class, RETURNS_DEEP_STUBS);
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(unidade.getBloco().getCondominio().getId()).thenReturn(condo);
        when(unidade.getId()).thenReturn(unitId);
        var cadastro = new CadastroUsuarioRequest("00000000000","Pessoa teste","pessoa@example.test","15999999999",1,103);
        when(usuarios.cadastrar(cadastro)).thenReturn(new com.eocape.backend.usuario.dto.response.UsuarioResponse(residentId,"Pessoa teste","pessoa@example.test","15999999999",false));
        controller.cadastrar(condo,user,new MoradorController.CadastroMorador(cadastro,unitId,"PROPRIETARIO"));
        var captor = ArgumentCaptor.forClass(VinculoUnidadeRequest.class);
        verify(vinculoService).criar(eq(user),captor.capture());
        assertEquals("PROPRIETARIO",captor.getValue().tipoVinculo());
        assertTrue(captor.getValue().principal());
        assertEquals("INQUILINO",new MoradorController.CadastroMorador(cadastro,unitId).tipoVinculo());
    }
    /**
     * Verifica que consulta somente vinculos do condominio solicitado.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Test void consultaSomenteVinculosDoCondominioSolicitado() {
        when(vinculos.findByUnidadeBlocoCondominioId(condo)).thenReturn(List.of());
        assertTrue(controller.listar(condo, user).isEmpty());
        verify(acesso).exigirGestor(user, condo);
        verify(vinculos).findByUnidadeBlocoCondominioId(condo);
        verifyNoMoreInteractions(vinculos);
    }
    /**
     * Verifica que rejeita cadastro em unidade de outro condominio antes de criar usuario.
     *
     * @throws org.springframework.web.server.ResponseStatusException se ocorrer a condição: A unidade pertence a outro condomínio.
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     */
    @Test void rejeitaCadastroEmUnidadeDeOutroCondominioAntesDeCriarUsuario() {
        UUID unitId = UUID.randomUUID();
        var unidade = mock(Unidade.class, RETURNS_DEEP_STUBS);
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(unidade.getBloco().getCondominio().getId()).thenReturn(UUID.randomUUID());
        var cadastro = new CadastroUsuarioRequest("00000000000", "Pessoa teste", "pessoa@example.test", "15999999999", 1, 101);
        assertThrows(ResponseStatusException.class, () -> controller.cadastrar(condo, user, new MoradorController.CadastroMorador(cadastro, unitId)));
        verifyNoInteractions(usuarios, vinculoService);
    }
}
