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
class VinculoUnidadeServiceTests {
    final VinculoUnidadeRepository vinculos = mock(VinculoUnidadeRepository.class);
    final UsuarioRepository usuarios = mock(UsuarioRepository.class);
    final UnidadeRepository unidades = mock(UnidadeRepository.class);
    final AcessoCondominioService acesso = mock(AcessoCondominioService.class);
    final UUID userId = UUID.randomUUID(), unitId = UUID.randomUUID(), manager = UUID.randomUUID();
    final VinculoUnidadeService service = new VinculoUnidadeService(vinculos,usuarios,unidades,acesso);
    void prepare() {
        var usuario = mock(Usuario.class); when(usuario.getId()).thenReturn(userId);
        var unidade = mock(Unidade.class,RETURNS_DEEP_STUBS); when(unidade.getId()).thenReturn(unitId);
        when(usuarios.findById(userId)).thenReturn(Optional.of(usuario));
        when(unidades.findById(unitId)).thenReturn(Optional.of(unidade));
        when(vinculos.findByUsuarioId(userId)).thenReturn(List.of());
    }
    @Test void aceitaSomenteOsTresTiposDoSchema() {
        prepare();
        when(vinculos.saveAndFlush(any())).thenAnswer(i -> i.getArgument(0));
        for (String tipo : List.of("PROPRIETARIO","INQUILINO","DEPENDENTE")) {
            assertEquals(tipo,service.criar(manager,new VinculoUnidadeRequest(userId,unitId,tipo,true,true)).tipoVinculo());
        }
        assertThrows(IllegalArgumentException.class, () -> service.criar(manager,new VinculoUnidadeRequest(userId,unitId,"MORADOR",true,true)));
        verify(vinculos,times(3)).saveAndFlush(any());
    }
    @Test void erroDeIntegridadeNaoViraConflitoDePrincipal() {
        prepare();
        when(vinculos.saveAndFlush(any())).thenThrow(new DataIntegrityViolationException("constraint failure"));
        var error = assertThrows(IllegalStateException.class, () -> service.criar(manager,new VinculoUnidadeRequest(userId,unitId,"INQUILINO",true,true)));
        assertFalse(error.getMessage().contains("principal")); assertTrue(error.getMessage().contains("vínculo"));
    }
}
