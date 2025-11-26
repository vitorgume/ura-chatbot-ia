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
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

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
    void deveEnviarMensagemRemovendoAspasERomovendoEspacos() {
        String msg = "\"olá\\n mundo\"";

        useCase.enviarMensagem(msg, telefone, true);

        verify(gateway).enviar("olá  mundo", telefone);
    }

    @Test
    void deveEnviarMensagemVaziaQuandoMensagemForNull() {
        useCase.enviarMensagem(null, telefone, true);
        verify(gateway).enviar("", telefone);
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
    void deveEnviarContatoVendedorComSucesso() {
        String msgDados = "DADOS";
        String msgSep   = "----";
        when(mensagemBuilder.getMensagem(
                TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente
        )).thenReturn(msgDados);
        when(mensagemBuilder.getMensagem(
                TipoMensagem.MENSAGEM_SEPARACAO, null, null
        )).thenReturn(msgSep);

        try (MockedStatic<CompletableFuture> cf = mockStatic(CompletableFuture.class)) {
            cf.when(() -> CompletableFuture.runAsync(any(Runnable.class)))
                    .thenAnswer(inv -> {
                        Runnable r = inv.getArgument(0);
                        r.run();
                        return CompletableFuture.completedFuture(null);
                    });

            useCase.enviarContatoVendedor(vendedor, cliente);
        }

        InOrder ord = inOrder(gateway);
        ord.verify(gateway).enviarContato(vendedor.getTelefone(), cliente);
        ord.verify(gateway).enviar(msgDados, vendedor.getTelefone());
        ord.verify(gateway).enviar(msgSep, vendedor.getTelefone());
        ord.verifyNoMoreInteractions();
    }

    @Test
    void deveIgnorarErroAoConstruirMensagemDeContato() {
        when(mensagemBuilder.getMensagem(
                TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente
        )).thenThrow(new RuntimeException("fail-builder"));

        try (MockedStatic<CompletableFuture> cf = mockStatic(CompletableFuture.class)) {
            cf.when(() -> CompletableFuture.runAsync(any(Runnable.class)))
                    .thenAnswer(inv -> {
                        Runnable r = inv.getArgument(0);
                        r.run();
                        return CompletableFuture.completedFuture(null);
                    });

            assertDoesNotThrow(() -> useCase.enviarContatoVendedor(vendedor, cliente));
        }

        verifyNoInteractions(gateway);
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
