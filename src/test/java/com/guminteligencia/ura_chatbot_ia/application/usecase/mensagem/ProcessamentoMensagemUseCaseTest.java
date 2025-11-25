package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ContextoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ProcessamentoContextoNovoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador.ContextoValidadorComposite;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoMensagemUseCaseTest {

    @Mock
    private MensageriaUseCase mensageriaUseCase;

    @Mock
    private ContextoUseCase contextoUseCase;

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private ContextoValidadorComposite contextoValidadorComposite;

    @Mock
    private ProcessamentoContextoExistente processamentoContextoExistente;

    @Mock
    private ProcessamentoContextoNovoUseCase processamentoContextoNovoUseCase;

    @InjectMocks
    private ProcessamentoMensagemUseCase useCase;

    private Contexto ctx1, ctx2;
    private Message msg1, msg2;
    private UUID id1, id2;
    private final String tel1 = "+5511999000111";

    @BeforeEach
    void setup() {
        ctx1 = mock(Contexto.class);
        ctx2 = mock(Contexto.class);
        msg1 = mock(Message.class);
        msg2 = mock(Message.class);
        id1 = UUID.randomUUID();
        id2 = UUID.randomUUID();
    }

    @Test
    void deveNaoFazerNadaQuandoNenhumContextoNaFila() {
        when(mensageriaUseCase.listarAvisos()).thenReturn(List.of());

        useCase.consumirFila();

        verify(mensageriaUseCase).listarAvisos();
        verifyNoMoreInteractions(
                mensageriaUseCase,
                contextoUseCase,
                clienteUseCase,
                contextoValidadorComposite,
                processamentoContextoExistente,
                processamentoContextoNovoUseCase
        );
    }

    @Test
    void deveProcessarFluxoExistenteComSucesso() {
        when(mensageriaUseCase.listarAvisos()).thenReturn(List.of(ctx1, ctx2));
        when(contextoValidadorComposite.permitirProcessar(ctx1)).thenReturn(true);
        when(contextoValidadorComposite.permitirProcessar(ctx2)).thenReturn(false);
        when(ctx1.getId()).thenReturn(id1);
        when(ctx1.getMensagemFila()).thenReturn(msg1);
        when(ctx1.getTelefone()).thenReturn(tel1);

        Cliente cliente = mock(Cliente.class);
        when(clienteUseCase.consultarPorTelefone(tel1))
                .thenReturn(Optional.of(cliente));

        useCase.consumirFila();

        InOrder ord = inOrder(
                mensageriaUseCase,
                processamentoContextoExistente,
                mensageriaUseCase,
                contextoUseCase
        );

        ord.verify(mensageriaUseCase).listarAvisos();
        ord.verify(processamentoContextoExistente)
                .processarContextoExistente(cliente, ctx1);
        ord.verify(mensageriaUseCase).deletarMensagem(msg1);
        ord.verify(contextoUseCase).deletar(id1);

        verify(contextoValidadorComposite, times(2)).permitirProcessar(ctx2);
        verifyNoMoreInteractions(
                processamentoContextoExistente,
                processamentoContextoNovoUseCase
        );
    }

    @Test
    void deveProcessarNovoFluxoComSucessoQuandoClienteNaoEncontrado() {
        when(mensageriaUseCase.listarAvisos()).thenReturn(List.of(ctx1));
        when(contextoValidadorComposite.permitirProcessar(ctx1)).thenReturn(true);
        when(ctx1.getId()).thenReturn(id1);
        when(ctx1.getMensagemFila()).thenReturn(msg1);
        when(ctx1.getTelefone()).thenReturn(tel1);

        when(clienteUseCase.consultarPorTelefone(tel1))
                .thenReturn(Optional.empty());

        useCase.consumirFila();

        InOrder ord = inOrder(
                mensageriaUseCase,
                processamentoContextoNovoUseCase,
                mensageriaUseCase,
                contextoUseCase
        );

        ord.verify(mensageriaUseCase).listarAvisos();
        ord.verify(processamentoContextoNovoUseCase).processarContextoNovo(ctx1);
        ord.verify(mensageriaUseCase).deletarMensagem(msg1);
        ord.verify(contextoUseCase).deletar(id1);

        verify(processamentoContextoExistente, never()).processarContextoExistente(any(), any());
        verifyNoMoreInteractions(
                processamentoContextoNovoUseCase,
                mensageriaUseCase,
                contextoUseCase
        );
    }
}