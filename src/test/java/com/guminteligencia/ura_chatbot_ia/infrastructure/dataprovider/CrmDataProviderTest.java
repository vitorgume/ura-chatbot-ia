package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
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

import java.util.Optional;
import java.util.function.Function;
import java.util.List;

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

    @Test
    void consultaLeadPeloTelefone_deveRetornarLeadMaisRecente() {
        var newer = new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto(
                null, null, null, null, null, null, null, null,
                null, 20L, null, null, null,
                new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto.Embedded(
                        null, null, List.of(new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto.LeadRef(999))
                )
        );

        var older = new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto(
                null, null, null, null, null, null, null, null,
                null, 10L, null, null, null,
                new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto.Embedded(
                        null, null, List.of(new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto.LeadRef(123))
                )
        );

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(older, newer));
        ContactsResponse response = new ContactsResponse(embedded);

        when(webClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(eq(ContactsResponse.class)))
                .thenReturn(Mono.just(response));

        Optional<Integer> out = provider.consultaLeadPeloTelefone(" 55 11 9999-9999 ");

        assertTrue(out.isPresent());
        assertEquals(999, out.get());
    }

    @Test
    void consultaLeadPeloTelefone_deveRetornarEmptyQuandoSemLeadsValidos() {
        var contatoSemLead = new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto(
                null, null, null, null, null, null, null, null,
                null, 15L, null, null, null,
                new com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto.Embedded(
                        null, null, List.of()
                )
        );

        ContactsResponse.Embedded embedded = new ContactsResponse.Embedded();
        embedded.setContacts(List.of(contatoSemLead));
        ContactsResponse response = new ContactsResponse(embedded);

        when(webClient.get()
                .uri(any(Function.class))
                .retrieve()
                .bodyToMono(eq(ContactsResponse.class)))
                .thenReturn(Mono.just(response));

        Optional<Integer> out = provider.consultaLeadPeloTelefone("11999999999");
        assertTrue(out.isEmpty());
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
}
