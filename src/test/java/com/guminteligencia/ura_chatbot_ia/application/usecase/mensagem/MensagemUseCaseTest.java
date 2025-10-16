package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensagemUseCaseTest {

    @Mock
    private MensagemGateway gateway;

    @Mock
    private MensagemBuilder mensagemBuilder;

    @InjectMocks
    private MensagemUseCase useCase;

    private final String texto = "texto qualquer";
    private final String telefone = "+5511999000111";

    private Vendedor vendedor;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        cliente = Cliente.builder()
                .id(java.util.UUID.randomUUID())
                .nome("ClienteX")
                .telefone(telefone)
                .build();

        vendedor = Vendedor.builder()
                .id(1L)
                .nome("VendedorY")
                .telefone("+5511888777666")
                .build();
    }

    @Test
    void deveEnviarMensagemParaTelefoneComSucesso() {
        useCase.enviarMensagem(texto, telefone, false);

        verify(gateway, times(1)).enviar(texto, telefone);
    }

    @Test
    void deveLancarExceptionQuandoGatewayFalharEnviarMensgaem() {
        doThrow(new IllegalStateException("erro-enviar")).when(gateway).enviar(texto, telefone);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.enviarMensagem(texto, telefone, false)
        );
        assertEquals("erro-enviar", ex.getMessage());
    }

    @Test
    void deveEnviarRelatorioComSucesso() {
        String arquivo = "base64xxx";
        String nomeArquivo = "rel.xlsx";

        useCase.enviarRelatorio(arquivo, nomeArquivo, telefone);

        verify(gateway, times(1))
                .enviarRelatorio(arquivo, nomeArquivo, telefone);
    }

    @Test
    void deveLancarExceptionQuandoEnvioDeRelatorioFalhar() {
        doThrow(new IllegalArgumentException("erro-rel"))
                .when(gateway).enviarRelatorio(anyString(), anyString(), anyString());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.enviarRelatorio("a", "b", "c")
        );
        assertEquals("erro-rel", ex.getMessage());

        verify(gateway).enviarRelatorio("a", "b", "c");
    }
}