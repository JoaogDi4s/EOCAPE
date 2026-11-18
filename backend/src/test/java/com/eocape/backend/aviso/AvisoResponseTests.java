package com.eocape.backend.aviso;
import com.eocape.backend.aviso.dto.response.AvisoResponse;
import com.eocape.backend.condominio.Condominio;
import com.eocape.backend.usuario.Usuario;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Verifica os cenários de aviso response e as regras exercitadas pelos testes desta classe.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
class AvisoResponseTests {
 /**
  * Verifica que permite aviso geral sem bloco.
  */
 @Test void permiteAvisoGeralSemBloco(){
  var condominio=mock(Condominio.class);var autor=mock(Usuario.class);
  var aviso=new Aviso(condominio,autor,null,"Aviso geral","Para todos os moradores",true);
  var response=AvisoResponse.from(aviso);
  assertNull(response.blocoId());assertEquals("Aviso geral",response.titulo());assertTrue(response.importante());
 }
}
