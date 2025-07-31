package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
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
        executor = new WebClientExecutor(webClient);
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

}