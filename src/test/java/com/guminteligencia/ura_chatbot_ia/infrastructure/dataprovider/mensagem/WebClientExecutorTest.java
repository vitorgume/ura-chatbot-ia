package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebClientExecutorTest {

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

    @Mock
    HttpHeaders httpHeadersMock;

    WebClientExecutor executor;

    @BeforeEach
    void setup() {
        executor = new WebClientExecutor(webClient, Retry.fixedDelay(3, Duration.ZERO));

    }

    @Test
    void deveExecutrPostComSucesso() {
        String uri = "http://api.test/send";
        Object payload = Map.of("k", "v");
        Map<String, String> headers = Map.of("h", "v");
        String errorMsg = "err-msg";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any(Consumer.class)))
                .thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec)
                .when(requestBodySpec)
                .bodyValue(payload);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        String result = executor.execute(uri, payload, headers, errorMsg, HttpMethod.POST);

        assertEquals("OK", result);

        verify(webClient).method(HttpMethod.POST);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).headers(any());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<HttpHeaders>> cap = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodySpec).headers(cap.capture());
        cap.getValue().accept(httpHeadersMock);
        verify(httpHeadersMock).add("h", "v");

        verify(requestBodySpec).bodyValue(payload);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveExecutarAoLancarExcetion() {
        String uri = "http://api.test/fail";
        Object payload = "x";
        Map<String, String> headers = Map.of();
        String errorMsg = "failure";

        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);

        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.error(new RuntimeException("panic")));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, payload, headers, errorMsg, HttpMethod.GET)
        );
        assertEquals("failure", ex.getMessage());
        assertNotNull(ex.getCause());
    }

    @Test
    void invocaPostNoExecuter() {
        String uri = "http://api.test/send";
        Object body = Map.of("k", "v");
        Map<String, String> hdrs = Map.of("h", "v");
        String err = "err-msg";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec)
                .when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OK"));

        String resp = executor.post(uri, body, hdrs, err);
        assertEquals("OK", resp);

        verify(webClient).method(HttpMethod.POST);
        verify(requestBodySpec).bodyValue(body);
    }

    @Test
    void deveExecutarGetSemBodyComSucesso() {
        String uri = "http://api.test/status";
        Map<String, String> headers = Map.of();
        String err = "err";

        when(webClient.method(HttpMethod.GET)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKGET"));

        String out = executor.execute(uri, null, headers, err, HttpMethod.GET);

        assertEquals("OKGET", out);
        verify(webClient).method(HttpMethod.GET);
        verify(requestBodyUriSpec).uri(uri);
        verify(requestBodySpec).headers(any());
        verify(requestBodySpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveLancarDataProviderExceptionQuandoUriFalhaAntesDoRetrieve() {
        String uri = "http://api.test/fail-sync";
        String err = "sync-failure";

        when(webClient.method(HttpMethod.DELETE)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenThrow(new IllegalStateException("boom"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> executor.execute(uri, null, Map.of(), err, HttpMethod.DELETE)
        );

        assertEquals(err, ex.getMessage());
        assertNull(ex.getCause());
        verify(webClient).method(HttpMethod.DELETE);
        verify(requestBodyUriSpec).uri(uri);
        verifyNoMoreInteractions(webClient, requestBodyUriSpec, requestBodySpec, requestHeadersSpec, responseSpec);
    }

    @Test
    void deveExecutarPutComBodyEHeaders() {
        String uri = "http://api.test/update";
        Object body = Map.of("a","b");
        Map<String, String> headers = Map.of("h1","v1","h2","v2");
        String err = "err";

        when(webClient.method(HttpMethod.PUT)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("OKPUT"));

        String out = executor.execute(uri, body, headers, err, HttpMethod.PUT);
        assertEquals("OKPUT", out);

        // valida headers aplicados
        @SuppressWarnings("unchecked")
        ArgumentCaptor<Consumer<HttpHeaders>> cap = ArgumentCaptor.forClass(Consumer.class);
        verify(requestBodySpec).headers(cap.capture());
        cap.getValue().accept(httpHeadersMock);
        verify(httpHeadersMock).add("h1","v1");
        verify(httpHeadersMock).add("h2","v2");

        verify(requestBodySpec).bodyValue(body);
        verify(requestHeadersSpec).retrieve();
        verify(responseSpec).bodyToMono(String.class);
    }

    @Test
    void deveRetentarQuandoFalharDuasVezesESucessoNaTerceira() {
        String uri = "http://api.test/retry";
        Object body = "x";
        Map<String, String> headers = Map.of();
        String err = "err";

        when(webClient.method(HttpMethod.POST)).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(uri)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        doReturn(requestHeadersSpec).when(requestBodySpec).bodyValue(body);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        AtomicInteger subs = new AtomicInteger(0);
        Mono<String> flaky = Mono.defer(() -> {
            int n = subs.incrementAndGet();
            if (n < 3) return Mono.error(new RuntimeException("fail-" + n));
            return Mono.just("OK");
        });
        when(responseSpec.bodyToMono(String.class)).thenReturn(flaky);

        var zeroBackoff = Retry.fixedDelay(3, Duration.ZERO);

        try (MockedStatic<Retry> mocked = Mockito.mockStatic(Retry.class)) {
            mocked.when(() -> Retry.backoff(eq(3), any(Duration.class)))
                    .thenReturn(zeroBackoff);

            String out = executor.execute(uri, body, headers, err, HttpMethod.POST);
            assertEquals("OK", out);
            assertEquals(3, subs.get(), "deveria ter 3 subscrições (1 inicial + 2 retries)");
        }
    }

}