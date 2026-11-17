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
class ArquivoDownloadTests {
    final ArquivoRepository arquivos = mock(ArquivoRepository.class);
    final ArquivoAtaRepository atas = mock(ArquivoAtaRepository.class);
    final AcessoCondominioService acesso = mock(AcessoCondominioService.class);
    final SupabaseStorageService storage = mock(SupabaseStorageService.class);
    final UUID morador = UUID.randomUUID(), condominio = UUID.randomUUID(), fileId = UUID.randomUUID();
    final ArquivoService service = new ArquivoService(arquivos,mock(CondominioRepository.class),mock(UsuarioRepository.class),acesso,storage,atas);
    void prepare() {
        var arquivo = mock(Arquivo.class,RETURNS_DEEP_STUBS);
        when(arquivo.getCondominio().getId()).thenReturn(condominio);
        when(arquivo.getEnviadoPor().getId()).thenReturn(UUID.randomUUID());
        when(arquivo.getChaveArmazenamento()).thenReturn("ata.pdf");
        when(arquivos.findById(fileId)).thenReturn(Optional.of(arquivo));
    }
    @Test void moradorBaixaAnexoDeAtaPublicadaNoSeuCondominio() {
        prepare(); when(atas.existsByArquivoIdAndAtaPublicadaTrueAndAtaCondominioId(fileId,condominio)).thenReturn(true);
        when(storage.gerarUrlAssinada("ata.pdf",300)).thenReturn("https://storage.example.test/ata.pdf");
        assertEquals("https://storage.example.test/ata.pdf",service.gerarUrlDownload(morador,fileId));
        verify(acesso).exigirVinculo(morador,condominio);
    }
    @Test void moradorNaoBaixaRascunhoOuArquivoPrivadoDeOutraPessoa() {
        prepare();
        assertThrows(AccessDeniedException.class, () -> service.gerarUrlDownload(morador,fileId));
        verifyNoInteractions(storage);
    }
    @Test void ataPublicadaNaoAutorizaPessoaDeOutroCondominio() {
        prepare(); doThrow(new AccessDeniedException("Sem vínculo")).when(acesso).exigirVinculo(morador,condominio);
        assertThrows(AccessDeniedException.class, () -> service.gerarUrlDownload(morador,fileId));
        verifyNoInteractions(atas,storage);
    }
}
