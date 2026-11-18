package com.eocape.backend.arquivo;
import com.eocape.backend.acesso.AcessoCondominioService;
import com.eocape.backend.condominio.CondominioRepository;
import com.eocape.backend.storage.SupabaseStorageService;
import com.eocape.backend.usuario.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
/**
 * Verifica os cenários de arquivo download e as regras exercitadas pelos testes desta classe.
 *
 * @author Equipe EOCAPE
 * @version 0.0.1-SNAPSHOT
 */
class ArquivoDownloadTests {
    /**
     * Repositório de persistência de arquivos.
     */
    final ArquivoRepository arquivos = mock(ArquivoRepository.class);
    /**
     * Repositório de persistência de anexos de atas.
     */
    final ArquivoAtaRepository atas = mock(ArquivoAtaRepository.class);
    /**
     * Serviço de verificação dos vínculos e das permissões no condomínio.
     */
    final AcessoCondominioService acesso = mock(AcessoCondominioService.class);
    /**
     * Serviço responsável por supabase storage.
     */
    final SupabaseStorageService storage = mock(SupabaseStorageService.class);
    /**
     * Identificador de file.
     */
    /**
     * Identificador de condominio.
     */
    /**
     * Identificador de morador.
     */
    final UUID morador = UUID.randomUUID(), condominio = UUID.randomUUID(), fileId = UUID.randomUUID();
    /**
     * Serviço responsável por arquivos.
     */
    final ArquivoService service = new ArquivoService(arquivos,mock(CondominioRepository.class),mock(UsuarioRepository.class),acesso,storage,atas);
    /**
     * Prepara as dependências e os dados necessários a cada cenário de teste.
     */
    void prepare() {
        var arquivo = mock(Arquivo.class,RETURNS_DEEP_STUBS);
        when(arquivo.getCondominio().getId()).thenReturn(condominio);
        when(arquivo.getEnviadoPor().getId()).thenReturn(UUID.randomUUID());
        when(arquivo.getChaveArmazenamento()).thenReturn("ata.pdf");
        when(arquivos.findById(fileId)).thenReturn(Optional.of(arquivo));
    }
    /**
     * Verifica que morador baixa anexo de ata publicada no seu condominio.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este arquivo não está disponível para o seu acesso.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @Test void moradorBaixaAnexoDeAtaPublicadaNoSeuCondominio() {
        prepare(); when(atas.existsByArquivoIdAndAtaPublicadaTrueAndAtaCondominioId(fileId,condominio)).thenReturn(true);
        when(storage.gerarUrlAssinada("ata.pdf",300)).thenReturn("https://storage.example.test/ata.pdf");
        assertEquals("https://storage.example.test/ata.pdf",service.gerarUrlDownload(morador,fileId));
        verify(acesso).exigirVinculo(morador,condominio);
    }
    /**
     * Verifica que morador nao baixa rascunho ou arquivo privado de outra pessoa.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Este arquivo não está disponível para o seu acesso.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @Test void moradorNaoBaixaRascunhoOuArquivoPrivadoDeOutraPessoa() {
        prepare();
        assertThrows(AccessDeniedException.class, () -> service.gerarUrlDownload(morador,fileId));
        verifyNoInteractions(storage);
    }
    /**
     * Verifica que ata publicada nao autoriza pessoa de outro condominio.
     *
     * @throws org.springframework.security.access.AccessDeniedException se ocorrer a condição: Sem vínculo.
     * @throws jakarta.persistence.EntityNotFoundException se ocorrer a condição: Arquivo não encontrado: .
     */
    @Test void ataPublicadaNaoAutorizaPessoaDeOutroCondominio() {
        prepare(); doThrow(new AccessDeniedException("Sem vínculo")).when(acesso).exigirVinculo(morador,condominio);
        assertThrows(AccessDeniedException.class, () -> service.gerarUrlDownload(morador,fileId));
        verifyNoInteractions(atas,storage);
    }
}
