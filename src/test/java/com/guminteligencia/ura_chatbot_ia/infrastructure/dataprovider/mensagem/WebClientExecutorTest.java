package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebClientExecutorTest {

    @Mock WebClient webClient;
    @Mock WebClient.RequestBodyUriSpec requestBodyUriSpec;
    @Mock WebClient.RequestBodySpec requestBodySpec;
    @Mock WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    @Mock WebClient.ResponseSpec responseSpec;
    @Mock HttpHeaders httpHeadersMock;

    WebClientExecutor executor;

    @BeforeEach
    void setup() {
        executor = new WebClientExecutor(webClient, Retry.fixedDelay(3, Duration.ZERO));
    }

    @Test
    void deveExecutarPostComSucesso() {
        String uri = "http://api.test/send";
        Object payload = Map.of("k", "v");
        Map<String, String> headers = Map.of("h", "v");
        String errorMsg = "err-msg";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(payload);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        String result = executor.execute(uri, payload, headers, errorMsg, HttpMethod.POST);

        assertEquals("OK", result);

        verify(webClient).method(HttpMethod.POST);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        verify(requestBodySpec).headers(any());
        verify(requestBodySpec).bodyValue(payload);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveExecutarGetComSucessoSemBody() {
        String uri = "http://api.test/status";
        Map<String, String> headers = Map.of();
        String err = "err";

        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKGET"));

        String out = executor.execute(uri, null, headers, err, HttpMethod.GET);

        assertEquals("OKGET", out);

        verify(webClient).method(HttpMethod.GET);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveExecutarPutComBodyEHeaders() {
        String uri = "http://api.test/update";
        Object body = Map.of("a", "b");
        Map<String, String> headers = Map.of("h1", "v1", "h2", "v2");
        String err = "err";

        when(webClient.method(HttpMethod.PUT)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKPUT"));

        String out = executor.execute(uri, body, headers, err, HttpMethod.PUT);
        assertEquals("OKPUT", out);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<HttpHeaders>> cap = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodySpec).headers(cap.capture());
        cap.getValue().accept(httpHeadersMock);
        verify(httpHeadersMock).add("h1", "v1");
        verify(httpHeadersMock).add("h2", "v2");

        verify(responseSpec).onStatus(any(), any());
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveLancarDataProviderExceptionQuandoOnStatusDetectaErroHttp() {
        String uri = "http://api.test/fail";
        Object payload = Map.of("x", 1);
        String errMsg = "failure";

        // retry que não re-tenta nada
        RetryBackoffSpec noRetry = Retry.fixedDelay(3, Duration.ZERO).filter(ex -> false);
        executor = new WebClientExecutor(webClient, noRetry);

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(payload);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        // Simula falha tratada pelo onStatus -> bodyToMono emite DataProviderException
        when(responseSpec.bodyToMono(String.class))
                .thenReturn(Mono.error(new DataProviderException(
                        "failure | HTTP 400 | Body: {\"error\":\"bad request\"}", null)));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, payload, Map.of(), errMsg, HttpMethod.POST)
        );

        assertTrue(ex.getMessage().contains("HTTP 400"));
        assertTrue(ex.getMessage().contains("Body"));
    }


    @Test
    void deveLancarDataProviderExceptionQuandoFalhaAntesDoRetrieve() {
        String uri = "http://api.test/fail-sync";
        String err = "sync-failure";

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenThrow(new IllegalStateException("boom"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, null, Map.of(), err, HttpMethod.DELETE)
        );

        assertTrue(ex.getMessage().startsWith(err));
        assertTrue(ex.getMessage().contains("cause="));
        verify(webClient).method(HttpMethod.DELETE);
    }

    @Test
    void deveRetentarQuandoFalharDuasVezesESucessoNaTerceira() {
        String uri = "http://api.test/retry";
        Object body = "x";
        Map<String, String> headers = Map.of();
        String err = "err";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE))
                .thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        AtomicInteger subs = new AtomicInteger(0);
        Mono<String> flaky = Mono.defer(() -> {
            int n = subs.incrementAndGet();
            if (n < 3) return Mono.error(new RuntimeException("fail-" + n));
            return Mono.just("OK");
        });
        when(responseSpec.bodyToMono(String.class)).thenReturn(flaky);

        String out = executor.execute(uri, body, headers, err, HttpMethod.POST);
        assertEquals("OK", out);
        assertEquals(3, subs.get(), "deveria ter 3 tentativas (1 inicial + 2 retries)");
    }

    @Test
    void onStatus_devePropagarDataProviderException_comBodyDeErro() {
        // Arrange: ExchangeFunction que SEMPRE responde 400 + JSON
        ExchangeFunction exchange = request -> {
            ClientResponse resp = ClientResponse
                    .create(HttpStatus.BAD_REQUEST)
                    .header(HttpHeaders.CONTENT_TYPE, "application/json")
                    .body("{\"error\":\"bad request\"}")
                    .build();
            return Mono.just(resp);
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchange)
                .build();

        // retry que não re-tenta, só para simplificar a asserção
        WebClientExecutor executor = new WebClientExecutor(webClient, Retry.fixedDelay(3, Duration.ZERO).filter(ex -> false));

        // Act + Assert
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(
                        "http://api.test/fake",
                        Map.of("x", 1),
                        Map.of("h", "v"),
                        "failure",
                        HttpMethod.POST)
        );

        // Verifica que a mensagem veio do handler do onStatus
        assertTrue(ex.getMessage().contains("failure | HTTP 400 | Body: {\"error\":\"bad request\"}"),
                "Mensagem deve conter o texto formatado pelo handler do onStatus");
    }

    @Test
    void onStatus_devePropagarDataProviderException_comBodyVazio_usandoDefaultIfEmpty() {
        // Arrange: ExchangeFunction que SEMPRE responde 404 SEM corpo
        ExchangeFunction exchange = request -> {
            ClientResponse resp = ClientResponse
                    .create(HttpStatus.NOT_FOUND)
                    // sem body
                    .build();
            return Mono.just(resp);
        };

        WebClient webClient = WebClient.builder()
                .exchangeFunction(exchange)
                .build();

        WebClientExecutor executor = new WebClientExecutor(webClient, Retry.fixedDelay(1, Duration.ZERO).filter(ex -> false));

        // Act + Assert
        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(
                        "http://api.test/notfound",
                        null,
                        Map.of(),
                        "not-found",
                        HttpMethod.GET)
        );

        // Verifica que entrou no defaultIfEmpty("<empty-body>")
        assertTrue(ex.getMessage().contains("not-found | HTTP 404 | Body: <empty-body>"),
                "Mensagem deve indicar body vazio tratado com <empty-body>");
    }

}