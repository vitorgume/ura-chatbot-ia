package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.ContatoRequestDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.DocumentoRequestDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemRequestWhatsAppDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MensagemDataProviderTest {

    @Mock
    WebClientExecutor executor;

    @InjectMocks
    MensagemDataProvider prodProvider;

    @InjectMocks
    MensagemDataProvider devProvider;

    final String token = "TK123";
    final String idInstance = "ID456";
    final String clienteToken = "CT789";

    @BeforeEach
    void setup() {
        prodProvider = new MensagemDataProvider(
                executor, token, idInstance, clienteToken, "prod"
        );
        devProvider = new MensagemDataProvider(
                executor, token, idInstance, clienteToken, "dev"
        );
    }

    @Test
    void deveEnviarEmProdChamarExecutorComParametoCorreto() {
        prodProvider.enviar("oi mundo", "+55999999999");

        ArgumentCaptor<String> uriCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<MensagemRequestWhatsAppDto> bodyCap = ArgumentCaptor.forClass(MensagemRequestWhatsAppDto.class);
        ArgumentCaptor<Map<String, String>> headersCap = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> errCap = ArgumentCaptor.forClass(String.class);

        verify(executor, times(1))
                .post(uriCap.capture(), bodyCap.capture(), headersCap.capture(), errCap.capture());

        String expectedUri =
                "https://api.z-api.io/instances/" + idInstance +
                        "/token/" + token + "/send-text";
        assertEquals(expectedUri, uriCap.getValue());

        MensagemRequestWhatsAppDto dto = bodyCap.getValue();
        assertEquals("+55999999999", dto.getPhone());
        assertEquals("oi mundo", dto.getMessage());

        assertEquals(Map.of("Client-Token", clienteToken), headersCap.getValue());
        assertEquals("Erro ao enviar mensagem.", errCap.getValue());
    }

    @Test
    void deveEnviarEmDevNaoDeveChamarExecutor() {
        devProvider.enviar("oi", "+55111111111");
        verifyNoInteractions(executor);
    }

    @Test
    void deveEnviarContatoEmProdDeveChamarExecutorComParametrosCorretos() {
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+55999999999").build();
        prodProvider.enviarContato("+55333333333", cliente.getTelefone(), cliente.getNome());

        ArgumentCaptor<String> uriCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<ContatoRequestDto> bodyCap = ArgumentCaptor.forClass(ContatoRequestDto.class);
        ArgumentCaptor<Map<String, String>> headersCap = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> errCap = ArgumentCaptor.forClass(String.class);

        verify(executor, times(1))
                .post(uriCap.capture(), bodyCap.capture(), headersCap.capture(), errCap.capture());

        String expectedUri =
                "https://api.z-api.io/instances/" + idInstance +
                        "/token/" + token + "/send-contact";
        assertEquals(expectedUri, uriCap.getValue());

        ContatoRequestDto dto = bodyCap.getValue();
        assertEquals("+55333333333", dto.getPhone());
        assertEquals("+55999999999", dto.getContactPhone());

        assertEquals(Map.of("Client-Token", clienteToken), headersCap.getValue());
        assertEquals("Erro ao enviar contato.", errCap.getValue());
    }

    @Test
    void deveEnviarContatoEmDevNaoDeveChamarExecutor() {
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).telefone("+55666666666").build();
        devProvider.enviarContato("+55444444444", cliente.getTelefone(), cliente.getNome());
        verifyNoInteractions(executor);
    }

    @Test
    void deveEnviarRelatorioEmProdDeveChamarExecutorComParametrosCorretoss() {
        String fakeBase64 = "R0lGODlhAQABAIAAAAUEBA==";
        prodProvider.enviarRelatorio(fakeBase64, "rel.xlsx", "+55777777777");

        ArgumentCaptor<String> uriCap = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<DocumentoRequestDto> bodyCap = ArgumentCaptor.forClass(DocumentoRequestDto.class);
        ArgumentCaptor<Map<String, String>> headersCap = ArgumentCaptor.forClass(Map.class);
        ArgumentCaptor<String> errCap = ArgumentCaptor.forClass(String.class);

        verify(executor, times(1))
                .post(uriCap.capture(), bodyCap.capture(), headersCap.capture(), errCap.capture());

        String expectedUri =
                "/instances/" + idInstance +
                        "/token/" + token + "/send-document/xlsx";
        assertEquals(expectedUri, uriCap.getValue());

        DocumentoRequestDto dto = bodyCap.getValue();
        assertEquals("+55777777777",
                dto.getPhone());
        assertEquals("rel.xlsx", dto.getFileName());

        assertEquals(Map.of("Client-Token", clienteToken), headersCap.getValue());
        assertEquals("Erro ao enviar relatório.", errCap.getValue());
    }

    @Test
    void deveEnviarRelatorioEmDevNaoDeveChamarExecutor() {
        devProvider.enviarRelatorio("AAA", "x.xlsx", "+55888888888");
        verifyNoInteractions(executor);
    }

}