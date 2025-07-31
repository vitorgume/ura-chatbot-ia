package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
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
        when(contexto.getMensagens()).thenReturn(List.of("msg1","msg2"));

        conversaAgente = mock(ConversaAgente.class);
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
    void processarContextoExistente_quandoAgenteFalha_propagates() {
        // arrange
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conversaAgente);
        when(agenteUseCase.enviarMensagem(any(), any(), anyList()))
                .thenThrow(new RuntimeException("erro-agente"));

        // act & assert
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processamentoContextoExistente.processarContextoExistente(cliente, contexto));
        assertEquals("erro-agente", ex.getMessage());
    }

    @Test
    void processarContextoExistente_quandoProcessoFalha_propagates() {
        // arrange
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conversaAgente);
        when(agenteUseCase.enviarMensagem(any(), any(), anyList()))
                .thenReturn("ok");
        when(processarContextoExistenteFactory.create("ok", conversaAgente))
                .thenReturn(processoMock);
        doThrow(new IllegalStateException("erro-processo"))
                .when(processoMock).processar("ok", conversaAgente, cliente);

        // act & assert
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> processamentoContextoExistente.processarContextoExistente(cliente, contexto));
        assertEquals("erro-processo", ex.getMessage());
        // garantir que não chegou a salvar após falha
        verify(conversaAgenteUseCase, never()).salvar(any());
    }
}