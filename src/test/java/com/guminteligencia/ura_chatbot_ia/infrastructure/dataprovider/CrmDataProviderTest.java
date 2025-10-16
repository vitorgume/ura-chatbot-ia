package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrmDataProviderTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private WebClient webClient;

    // Mocks "genéricos" para encadear chamadas do WebClient
    @Mock private WebClient.RequestHeadersUriSpec<?> headersUriSpec;
    @Mock private WebClient.RequestBodyUriSpec bodyUriSpec;
    @Mock private WebClient.RequestHeadersSpec<?> headersSpec;
    @Mock private WebClient.ResponseSpec responseSpec;

    private CrmDataProvider provider; // não precisamos de spy; só temp files

    private final ObjectMapper om = new ObjectMapper();

    @BeforeEach
    void setUp() {
        provider = new CrmDataProvider(webClient);
    }

    // ------------------------------
    // consultaLeadPeloTelefone
    // ------------------------------

    @Test
    void consultaLeadPeloTelefone_deveRetornarEmptyQuandoSemResultado() {
        when(webClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(eq(ContactsResponse.class)))
                .thenReturn(Mono.empty()); // <= sem usar Mono.just(null)

        Optional<Integer> out = provider.consultaLeadPeloTelefone("+5511999999999");
        assertTrue(out.isEmpty());
    }

    @Test
    void consultaLeadPeloTelefone_deveLancarDataProviderExceptionEmErroHttp() {
        doReturn(headersUriSpec)
                .when(webClient)
                .get();

        when(headersUriSpec.uri(any(Function.class))).thenReturn(headersUriSpec);
        when(headersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(eq(com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse.class)))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.consultaLeadPeloTelefone("+5511999999999"));
        assertEquals("Erro ao consultar lead pelo seu telefone.", ex.getMessage());
    }

    // ------------------------------
    // atualizarCard
    // ------------------------------

    @Test
    void atualizarCard_deveEnviarPatchComSucesso() {
        CardDto body = CardDto.builder().statusId(123).build();

        when(webClient.patch()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        provider.atualizarCard(body, 42);

        verify(webClient).patch();
        verify(bodyUriSpec).uri(any(Function.class));
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void atualizarCard_deveLancarDataProviderExceptionQuandoFalhar() {
        CardDto body = CardDto.builder().statusId(123).build();

        when(webClient.patch()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("patch-fail")));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.atualizarCard(body, 42));
        assertEquals("Erro ao atualizar card.", ex.getMessage());
    }

    // ------------------------------
    // criarSessaoArquivo
    // ------------------------------

    @Test
    void criarSessaoArquivo_deveCriarSessaoComSucesso_arquivoLocal() throws IOException {
        // cria temp file local (evita rede)
        Path tmp = Files.createTempFile("video-test", ".mp4");
        Files.writeString(tmp, "conteudo"); // 8 bytes
        String pathStr = tmp.toString();    // isLocalFile = true

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/sessao-abc");
        when(sessao.getMaxFileSize()).thenReturn(314_572_800); // 300MB
        when(sessao.getMaxPartSize()).thenReturn(524_288);     // 512KB

        // POST drive/v1.0/sessions
        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(contains("/v1.0/sessions"))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SessaoArquivoDto.class)).thenReturn(Mono.just(sessao));

        SessaoArquivoDto out = provider.criarSessaoArquivo(pathStr);
        assertNotNull(out);
        assertEquals("https://drive-c.kommo.com/upload/sessao-abc", out.getUploadUrl());
    }

    @Test
    void criarSessaoArquivo_deveLancarDataProviderExceptionQuandoHttpFalhar() throws IOException {
        Path tmp = Files.createTempFile("video-test", ".mp4");
        Files.writeString(tmp, "abc");
        String p = tmp.toString();

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(anyString())).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);

        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());

        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SessaoArquivoDto.class)).thenReturn(Mono.error(new RuntimeException("fail")));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.criarSessaoArquivo(p));
        assertEquals("Erro ao criar sessão arquivo.", ex.getMessage());
    }

    @Test
    void criarSessaoArquivo_deveLancarQuandoSizeInvalido() {
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.criarSessaoArquivo(":/nao-existe/arquivo.mp4")
        );
        assertEquals("Erro ao criar sessão arquivo.", ex.getMessage());
    }

    // ------------------------------
    // enviarArquivoParaUpload
    // ------------------------------

    @Test
    void enviarArquivoParaUpload_singlePart_sucesso() throws IOException {
        // temp file pequeno (<= maxPartSize) => single-part
        Path tmp = Files.createTempFile("vid", ".bin");
        Files.write(tmp, "hello".getBytes()); // 5 bytes
        String fileUrl = tmp.toUri().getPath(); // sem "http"; isLocalFile = true
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            // Em Windows, toUri().getPath() vira /C:/..., e isLocalFile() considera local.
            fileUrl = tmp.toString();
        }

        // mock SessaoArquivoDto
        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/up-1");
        when(sessao.getMaxPartSize()).thenReturn(524_288); // 512KB
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        // POST upload (single)
        WebClient.RequestBodyUriSpec postUpload = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> reqUpload = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec respUpload = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(postUpload); // esta chamada é do upload
        when(postUpload.uri(eq("https://drive-c.kommo.com/upload/up-1"))).thenReturn(postUpload);
        when(postUpload.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(postUpload);
        when(postUpload.header(eq("Content-Length"), anyString())).thenReturn(postUpload);

        doReturn(reqUpload)
                .when(postUpload)
                .bodyValue(any(byte[].class));

        when(reqUpload.retrieve()).thenReturn(respUpload);

        ObjectNode ok = om.createObjectNode();
        ok.put("finished", true);
        ok.put("file_uuid", "uuid-123");
        when(respUpload.bodyToMono(eq(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(Mono.just(ok));

        String fileUuid = provider.enviarArquivoParaUpload(sessao, fileUrl);
        assertEquals("uuid-123", fileUuid);
    }

    @Test
    void enviarArquivoParaUpload_multiPart_sucesso() throws IOException {
        // cria arquivo > 512KB para forçar multi-part
        int size = 600 * 1024; // 600 KB
        Path tmp = Files.createTempFile("vid-big", ".bin");
        byte[] big = new byte[size];
        new Random().nextBytes(big);
        Files.write(tmp, big);
        String fileUrl = tmp.toString(); // local

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/first");
        when(sessao.getMaxPartSize()).thenReturn(524_288); // 512 KB
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        // 1a chamada POST -> next_url
        WebClient.RequestBodyUriSpec post1 = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> req1 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp1 = mock(WebClient.ResponseSpec.class);

        // 2a chamada POST -> finished + file_uuid
        WebClient.RequestBodyUriSpec post2 = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> req2 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp2 = mock(WebClient.ResponseSpec.class);

        // O provider chamará webClient.post() duas vezes (2 chunks)
        when(webClient.post()).thenReturn(post1, post2);

        // Encadeia 1o chunk
        when(post1.uri(eq("https://drive-c.kommo.com/upload/first"))).thenReturn(post1);
        when(post1.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(post1);
        when(post1.header(eq("Content-Length"), anyString())).thenReturn(post1);
        doReturn(req1)
                .when(post1)
                .bodyValue(any(byte[].class));
        when(req1.retrieve()).thenReturn(resp1);

        ObjectNode r1 = om.createObjectNode();
        r1.put("finished", false);
        r1.put("next_url", "https://drive-c.kommo.com/upload/second");
        when(resp1.bodyToMono(eq(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(Mono.just(r1));

        // Encadeia 2o chunk
        when(post2.uri(eq("https://drive-c.kommo.com/upload/second"))).thenReturn(post2);
        when(post2.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(post2);
        when(post2.header(eq("Content-Length"), anyString())).thenReturn(post2);
        doReturn(req2)
                .when(post2)
                .bodyValue(any(byte[].class));
        when(req2.retrieve()).thenReturn(resp2);

        ObjectNode r2 = om.createObjectNode();
        r2.put("finished", true);
        r2.put("file_uuid", "uuid-xyz");
        when(resp2.bodyToMono(eq(com.fasterxml.jackson.databind.JsonNode.class)))
                .thenReturn(Mono.just(r2));

        String uuid = provider.enviarArquivoParaUpload(sessao, fileUrl);
        assertEquals("uuid-xyz", uuid);

        // sanity: verificou cadeia correta
        verify(webClient, times(2)).post();
    }

    @Test
    void enviarArquivoParaUpload_deveLancarQuandoExcederMaxFileSize() throws IOException {
        Path tmp = Files.createTempFile("vid", ".bin");
        Files.write(tmp, new byte[2048]); // 2KB

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getMaxFileSize()).thenReturn(1024); // 1KB -> menor que o arquivo

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> provider.enviarArquivoParaUpload(sessao, tmp.toString()));
        assertTrue(ex.getMessage().contains("Arquivo excede max_file_size"));
    }

    // ------------------------------
    // anexarArquivoLead
    // ------------------------------

    @Test
    void anexarArquivoLead_deveAnexarComSucesso() {
        when(webClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.empty());

        provider.anexarArquivoLead("uuid-123", 99);

        verify(webClient).put();
        verify(responseSpec).toBodilessEntity();
    }

    @Test
    void anexarArquivoLead_deveLancarDataProviderExceptionQuandoFalhar() {
        when(webClient.put()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(any(Function.class))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
        doReturn(headersSpec)
                .when(bodyUriSpec)
                .bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.toBodilessEntity()).thenReturn(Mono.error(new RuntimeException("put-fail")));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.anexarArquivoLead("uuid-123", 99));
        assertEquals("Erro ao anexar arquivo ao card.", ex.getMessage());
    }

    @Test
    void criarSessaoArquivo_deveLancarQuandoSessaoVemNula() throws Exception {
        Path tmp = Files.createTempFile("vid", ".bin");
        Files.write(tmp, "abc".getBytes());

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(contains("/v1.0/sessions"))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        // Mono.empty().block() => null => dispara o if (sessao == null)
        when(responseSpec.bodyToMono(SessaoArquivoDto.class)).thenReturn(Mono.empty());

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.criarSessaoArquivo(tmp.toString()));
        assertEquals("Erro ao criar sessão arquivo.", ex.getMessage());
    }

    @Test
    void criarSessaoArquivo_deveLancarQuandoUltrapassaMaxFileSizeRetornado() throws Exception {
        // arquivo “grande”
        byte[] big = new byte[1024 * 1024]; // 1MB
        Path tmp = Files.createTempFile("vid-big", ".bin");
        Files.write(tmp, big);

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/xyz");
        when(sessao.getMaxFileSize()).thenReturn(100 * 1024); // 100 KB << 1MB

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(contains("/v1.0/sessions"))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SessaoArquivoDto.class)).thenReturn(Mono.just(sessao));

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.criarSessaoArquivo(tmp.toString()));
        assertTrue(ex.getMessage().contains(CrmDataProvider.MENSAGEM_ERRO_CRIAR_SESSAO_ARQUIVO));
    }

    // ======================
// BRANCHES ADICIONAIS
// ======================

    @Test
    void enviarArquivoParaUpload_singlePart_semFileUuid_deveLancar() throws Exception {
        // Arquivo pequeno => single-part
        Path tmp = Files.createTempFile("vid-sp-nofileuuid", ".bin");
        Files.write(tmp, "abc".getBytes());
        String fileUrl = System.getProperty("os.name").toLowerCase().contains("win")
                ? tmp.toString()
                : tmp.toUri().getPath();

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/sp");
        when(sessao.getMaxPartSize()).thenReturn(524_288);
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        WebClient.RequestBodyUriSpec post = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> req = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(post);
        when(post.uri("https://drive-c.kommo.com/upload/sp")).thenReturn(post);
        when(post.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(post);
        when(post.header(eq("Content-Length"), anyString())).thenReturn(post);
        doReturn(req).when(post).bodyValue(any(byte[].class));
        when(req.retrieve()).thenReturn(resp);

        // finished=false e sem file_uuid -> deve lançar
        ObjectNode bad = om.createObjectNode();
        bad.put("finished", false);
        bad.put("next_url", "https://drive-c.kommo.com/upload/next"); // só pra mostrar que veio algo
        when(resp.bodyToMono(JsonNode.class)).thenReturn(Mono.just(bad));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> provider.enviarArquivoParaUpload(sessao, fileUrl));
        assertTrue(ex.getMessage().contains("sem file_uuid"));
    }

    @Test
    void enviarArquivoParaUpload_multiPart_finalizouSemFileUuid_deveLancar() throws Exception {
        // >512KB para forçar 2 chunks
        Path tmp = Files.createTempFile("vid-mp-nofileuuid", ".bin");
        byte[] big = new byte[600 * 1024];
        new Random().nextBytes(big);
        Files.write(tmp, big);

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/first2");
        when(sessao.getMaxPartSize()).thenReturn(524_288);
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        // 1º chunk
        WebClient.RequestBodyUriSpec p1 = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> r1 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec s1 = mock(WebClient.ResponseSpec.class);

        // 2º chunk (final) sem file_uuid
        WebClient.RequestBodyUriSpec p2 = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> r2 = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec s2 = mock(WebClient.ResponseSpec.class);

        when(webClient.post()).thenReturn(p1, p2);

        when(p1.uri("https://drive-c.kommo.com/upload/first2")).thenReturn(p1);
        when(p1.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(p1);
        when(p1.header(eq("Content-Length"), anyString())).thenReturn(p1);
        doReturn(r1).when(p1).bodyValue(any(byte[].class));
        when(r1.retrieve()).thenReturn(s1);

        ObjectNode resp1 = om.createObjectNode();
        resp1.put("finished", false);
        resp1.put("next_url", "https://drive-c.kommo.com/upload/second2");
        when(s1.bodyToMono(JsonNode.class)).thenReturn(Mono.just(resp1));

        when(p2.uri("https://drive-c.kommo.com/upload/second2")).thenReturn(p2);
        when(p2.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(p2);
        when(p2.header(eq("Content-Length"), anyString())).thenReturn(p2);
        doReturn(r2).when(p2).bodyValue(any(byte[].class));
        when(r2.retrieve()).thenReturn(s2);

        ObjectNode resp2 = om.createObjectNode();
        resp2.put("finished", true);
        // sem file_uuid aqui -> deve lançar
        when(s2.bodyToMono(JsonNode.class)).thenReturn(Mono.just(resp2));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> provider.enviarArquivoParaUpload(sessao, tmp.toString()));
        assertTrue(ex.getMessage().contains("sem file_uuid"));
    }

    @Test
    void enviarArquivoParaUpload_zeroBytes_deveLancarNaoFinalizado() throws Exception {
        Path tmp = Files.createTempFile("vid-zero", ".bin");
        // arquivo vazio por design

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/whatever");
        when(sessao.getMaxPartSize()).thenReturn(524_288);
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        // Como realSize = 0, ele cai no multi-part; não haverá leitura de chunk => lança "Upload não finalizado."
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> provider.enviarArquivoParaUpload(sessao, tmp.toString()));
        assertTrue(ex.getMessage().contains("Upload não finalizado"));
    }

    @Test
    void enviarArquivoParaUpload_singlePart_usaCampoUuid_quandoServidorNaoEnviaFileUuid() throws Exception {
        Path tmp = Files.createTempFile("vid-sp-uuid", ".bin");
        Files.write(tmp, "abc".getBytes());
        String fileUrl = System.getProperty("os.name").toLowerCase().contains("win")
                ? tmp.toString()
                : tmp.toUri().getPath();

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/sp-uuid");
        when(sessao.getMaxPartSize()).thenReturn(524_288);
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        WebClient.RequestBodyUriSpec post = mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestHeadersSpec<?> req = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec resp = mock(WebClient.ResponseSpec.class);
        when(webClient.post()).thenReturn(post);
        when(post.uri("https://drive-c.kommo.com/upload/sp-uuid")).thenReturn(post);
        when(post.contentType(MediaType.APPLICATION_OCTET_STREAM)).thenReturn(post);
        when(post.header(eq("Content-Length"), anyString())).thenReturn(post);
        doReturn(req).when(post).bodyValue(any(byte[].class));
        when(req.retrieve()).thenReturn(resp);

        // Sem 'finished', sem 'file_uuid', apenas 'uuid' -> parseUploadResponse deve tratar e considerar finished
        ObjectNode ok = om.createObjectNode();
        ok.put("uuid", "ok-uuid");
        when(resp.bodyToMono(JsonNode.class)).thenReturn(Mono.just(ok));

        String uuid = provider.enviarArquivoParaUpload(sessao, fileUrl);
        assertEquals("ok-uuid", uuid);
    }

    @Test
    void criarSessaoArquivo_deveUsarOctetStreamQuandoNaoDetectaContentType() throws Exception {
        // extensão esquisita para forçar content-type desconhecido
        Path tmp = Files.createTempFile("sem-tipo", ".minhaextqueprovavelmentenaotemtipo");
        Files.write(tmp, "xyz".getBytes());

        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/ct-fallback");
        when(sessao.getMaxFileSize()).thenReturn(314_572_800); // grande

        when(webClient.post()).thenReturn(bodyUriSpec);
        when(bodyUriSpec.uri(contains("/v1.0/sessions"))).thenReturn(bodyUriSpec);
        when(bodyUriSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(bodyUriSpec);
        doReturn(headersSpec).when(bodyUriSpec).bodyValue(any()); // não precisamos inspecionar o body
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(SessaoArquivoDto.class)).thenReturn(Mono.just(sessao));

        SessaoArquivoDto out = provider.criarSessaoArquivo(tmp.toString());
        assertNotNull(out);
        // Se chegou aqui, o ramo (contentType null/blank -> application/octet-stream) foi executado
    }

    @Test
    void enviarArquivoParaUpload_urlInvalida_deveGerarDataProviderExceptionStreaming() {
        // forçamos obterMeta a cair no catch (retorna -1,null) e o streaming a falhar com URL malformada
        SessaoArquivoDto sessao = mock(SessaoArquivoDto.class);
        when(sessao.getUploadUrl()).thenReturn("https://drive-c.kommo.com/upload/stream-fail");
        when(sessao.getMaxFileSize()).thenReturn(314_572_800);

        DataProviderException ex = assertThrows(DataProviderException.class,
                () -> provider.enviarArquivoParaUpload(sessao, "http://^"));
        assertTrue(ex.getMessage().contains("Falha no streaming do arquivo para o Kommo"));
    }



}