package com.eocape.backend.vinculoUnidade;
import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.unidade.*;
import com.eocape.backend.usuario.*;
import com.eocape.backend.vinculoUnidade.dto.VinculoUnidadeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Verifica os cenários de vinculo unidade service e as regras exercitadas pelos testes desta classe.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
class VinculoUnidadeServiceTests {
    /**
     * Repositório de persistência de vínculos entre usuários e unidades.
     */
    final VinculoUnidadeRepository vinculos = mock(VinculoUnidadeRepository.class);
    /**
     * Repositório de persistência de usuários.
     */
    final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    /**
     * Repositório de persistência de unidades.
     */
    final UnidadeRepository unidades = mock(UnidadeRepository.class);
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    final AcessoCondominioService acesso = mock(AcessoCondominioService.class);
    /**
     * Identificador de manager.
     */
    /**
     * Identificador de unit.
     */
    /**
     * Identificador de user.
     */
    final UUID userId = UUID.randomUUID(), unitId = UUID.randomUUID(), manager = UUID.randomUUID();
    /**
     * Serviço responsável por vínculos entre usuários e unidades.
     */
    final VinculoUnidadeService service = new VinculoUnidadeService(vinculos,usuarios,unidades,acesso);
    /**
     * Prepara as dependências e os dados necessários a cada cenário de teste.
     */
    void prepare() {
        var usuario = mock(Usuario.class); when(usuario.getId()).thenReturn(userId);
        var unidade = mock(Unidade.class,RETURNS_DEEP_STUBS); when(unidade.getId()).thenReturn(unitId);
        when(usuarios.findById(userId)).thenReturn(Optional.of(usuario));
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(vinculos.findByUsuarioId(userId)).thenReturn(List.of());
    }
    /**
     * Verifica que aceita somente os tres tipos do schema.
     *
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Test void aceitaSomenteOsTresTiposDoSchema() {
        prepare();
        when(vinculos.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        for (String tipo : List.of("PROPRIETARIO","INQUILINO","DEPENDENTE")) {
            assertEquals(tipo,service.criar(manager,new VinculoUnidadeRequest(userId,unitId,tipo,true,true)).tipoVinculo());
        }
        assertThrows(IllegalArgumentException.class, () -> service.criar(manager,new VinculoUnidadeRequest(userId,unitId,"MORADOR",true,true)));
        verify(vinculos,times(3)).saveAndFlush(any());
    }
    /**
     * Verifica que erro de integridade nao vira conflito de principal.
     *
     * @throws org.springframework.dao.DataIntegrityViolationException se ocorrer a condição: constraint failure.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Unidade não encontrada: .
     * @throws IllegalArgumentException se ocorrer a condição: Tipo de vínculo inválido. Escolha proprietário, inquilino ou dependente..
     * @throws IllegalStateException se ocorrer a condição: Não foi possível salvar o vínculo: os dados violam uma regra do cadastro. Verifique se a unidade já está associada ao morador..
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Apenas síndico ou administrador pode executar esta ação.
     */
    @Test void erroDeIntegridadeNaoViraConflitoDePrincipal() {
        prepare();
        when(vinculos.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("constraint failure"));
        var error = assertThrows(IllegalStateException.class, () -> service.criar(manager,new VinculoUnidadeRequest(userId,unitId,"INQUILINO",true,true)));
        assertFalse(error.getMessage().contains("principal")); assertTrue(error.getMessage().contains("vínculo"));
    }
}
