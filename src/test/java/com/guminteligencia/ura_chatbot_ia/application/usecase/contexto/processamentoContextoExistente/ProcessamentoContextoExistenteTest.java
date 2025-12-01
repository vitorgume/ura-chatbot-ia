package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import com.guminteligencia.ura_chatbot_ia.domain.StatusConversa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoContextoExistenteTest {

    @Mock
    private AgenteUseCase agenteUseCase;

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    private ProcessarContextoExistenteFactory processarContextoExistenteFactory;

    @Mock
    private ProcessamentoContextoExistenteType processoMock;

    @InjectMocks
    private ProcessamentoContextoExistente processamentoContextoExistente;

    private Cliente cliente;
    private Contexto contexto;
    private ConversaAgente conversaAgente;

    @BeforeEach
    void setup() {
        cliente = mock(Cliente.class);
        when(cliente.getId()).thenReturn(UUID.randomUUID());

        contexto = mock(Contexto.class);
        when(contexto.getMensagens()).thenReturn(List.of(
                MensagemContexto.builder().mensagem("msg1").build(),
                MensagemContexto.builder().mensagem("msg2").build()
        ));

        conversaAgente = mock(ConversaAgente.class);
        when(conversaAgente.getStatus()).thenReturn(StatusConversa.ANDAMENTO);
    }

    @Test
    void deveProcessarContextoExistenteExecuntandoFluxoCompletoComSucesso() {
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conversaAgente);
        when(agenteUseCase.enviarMensagem(cliente, conversaAgente, contexto.getMensagens()))
                .thenReturn("resposta");
        when(processarContextoExistenteFactory.create("resposta", conversaAgente))
                .thenReturn(processoMock);

        processamentoContextoExistente.processarContextoExistente(cliente, contexto);

        verify(conversaAgenteUseCase).consultarPorCliente(cliente.getId());
        verify(agenteUseCase).enviarMensagem(cliente, conversaAgente, contexto.getMensagens());
        verify(processarContextoExistenteFactory).create("resposta", conversaAgente);
        verify(processoMock).processar("resposta", conversaAgente, cliente);

        verify(conversaAgente).setDataUltimaMensagem(any(LocalDateTime.class));
        verify(conversaAgenteUseCase).salvar(conversaAgente);
    }

    @Test
    void deveLancarExceptionQuandoComunicacaoComAgenteFalha() {
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conversaAgente);
        when(agenteUseCase.enviarMensagem(any(), any(), anyList()))
                .thenThrow(new RuntimeException("erro-agente"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processamentoContextoExistente.processarContextoExistente(cliente, contexto));
        assertEquals("erro-agente", ex.getMessage());
    }

    @Test
    void deveLancarExceptionAoProcessarContextoExistente() {
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conversaAgente);
        when(agenteUseCase.enviarMensagem(any(), any(), anyList()))
                .thenReturn("ok");
        when(processarContextoExistenteFactory.create("ok", conversaAgente))
                .thenReturn(processoMock);
        doThrow(new IllegalStateException("erro-processo"))
                .when(processoMock).processar("ok", conversaAgente, cliente);
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> processamentoContextoExistente.processarContextoExistente(cliente, contexto));
        assertEquals("erro-processo", ex.getMessage());

        verify(conversaAgenteUseCase, never()).salvar(any());
    }
}
