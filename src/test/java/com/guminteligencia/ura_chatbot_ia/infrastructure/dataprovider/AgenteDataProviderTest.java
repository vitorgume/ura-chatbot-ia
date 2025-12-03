package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgenteDataProviderTest {

    @Mock
    WebClient webClient;

    @Mock
    WebClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    WebClient.RequestBodySpec requestBodySpec;

    @Mock
    WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    WebClient.ResponseSpec responseSpec;

    @InjectMocks
    AgenteDataProvider provider;

    final String agenteUriApi = "http://agent";

    @BeforeEach
    void setup() {
        provider = new AgenteDataProvider(webClient, agenteUriApi);
    }

    private void stubFluentPost(String uri, Object body) {
        when(webClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(MediaType.APPLICATION_JSON))
                .thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
    }

    @Test
    void deveEnviarMensagemComSucesso() {
        MensagemAgenteDto dto = MensagemAgenteDto.builder()
                .clienteId("cli1")
                .conversaId("conv1")
                .mensagem("hello")
                .audiosUrl(List.of("a1"))
                .imagensUrl(List.of("i1"))
                .build();
        String uri = agenteUriApi + "/chat";
        Map<String, Object> expectedBody = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        stubFluentPost(uri, expectedBody);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("OK"));

        String result = provider.enviarMensagem(dto);

        assertEquals("OK", result);
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).bodyValue(expectedBody);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveLancarExceptionAoEnviarMensagem() {
        MensagemAgenteDto dto = MensagemAgenteDto.builder()
                .clienteId("cli1")
                .conversaId("conv1")
                .mensagem("hello")
                .audiosUrl(List.of("a1"))
                .imagensUrl(List.of("i1"))
                .build();
        String uri = agenteUriApi + "/chat";
        Map<String, Object> expectedBody = Map.of(
                "cliente_id", dto.getClienteId(),
                "conversa_id", dto.getConversaId(),
                "message", dto.getMensagem(),
                "audios_url", dto.getAudiosUrl(),
                "imagens_url", dto.getImagensUrl()
        );

        stubFluentPost(uri, expectedBody);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new RuntimeException("fail")));

        assertThrows(
                RuntimeException.class,
                () -> provider.enviarMensagem(dto)
        );
    }

    @Test
    void enviarJsonTrasformacaoComSucesso() {
        String texto = "some text";
        String uri = agenteUriApi + "/chat/json";
        Map<String, String> expectedBody = Map.of("mensagem", texto);

        stubFluentPost(uri, expectedBody);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.just("JSON_OK"));

        String result = provider.enviarJsonTrasformacao(texto);

        assertEquals("JSON_OK", result);
        verify(webClient).post();
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).contentType(MediaType.APPLICATION_JSON);
        verify(requestBodySpec).bodyValue(expectedBody);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveLancarExceptionAoEnviarJsonTransformacao() {
        String texto = "bad text";
        String uri = agenteUriApi + "/chat/json";
        Map<String, String> expectedBody = Map.of("mensagem", texto);

        stubFluentPost(uri, expectedBody);
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new RuntimeException("failJson")));

        assertThrows(
                RuntimeException.class,
                () -> provider.enviarJsonTrasformacao(texto)
        );
    }

}
