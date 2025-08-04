package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorTempoEsperaTest {

    @Mock
    ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    ClienteUseCase clienteUseCase;

    @Mock
    MensageriaUseCase mensageriaUseCase;

    @InjectMocks
    ValidadorTempoEspera validadorTempoEspera;

    @Mock
    Contexto ctx;

    @Mock
    Cliente cliente;

    @Mock
    ConversaAgente conv;

    final String tel = "+5511999000111";

    @BeforeEach
    void setup() {
        when(ctx.getTelefone()).thenReturn(tel);
    }

    @Test
    void deveRetornarFalseSeClienteNaoEncontrado() {
        when(clienteUseCase.consultarPorTelefone(tel)).thenReturn(Optional.empty());
        assertFalse(validadorTempoEspera.deveIgnorar(ctx));
        verifyNoInteractions(conversaAgenteUseCase, mensageriaUseCase);
    }

    @Test
    void deveRetornaFalseConversaNaoFinalizada() {
        when(clienteUseCase.consultarPorTelefone(tel)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);
        when(conv.getFinalizada()).thenReturn(false);

        assertTrue(validadorTempoEspera.deveIgnorar(ctx));
        verifyNoInteractions(mensageriaUseCase);
    }

    @Test
    void deveRetornarFalseEDeletarMensagemSeEstiverDentroDos30minutos() {
        when(clienteUseCase.consultarPorTelefone(tel)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);

        LocalDateTime fixed = LocalDateTime.of(2025,8,1,12,0);
        try (MockedStatic<LocalDateTime> mt = mockStatic(LocalDateTime.class)) {
            mt.when(LocalDateTime::now).thenReturn(fixed);
            when(conv.getFinalizada()).thenReturn(true);
            when(conv.getDataUltimaMensagem())
                    .thenReturn(fixed.minusMinutes(10));

            assertFalse(validadorTempoEspera.deveIgnorar(ctx));
            verify(mensageriaUseCase).deletarMensagem(ctx.getMensagemFila());
        }
    }

    @Test
    void deveRetornarTrueNaoDeletarSeMensagemDepois30minutos() {
        when(clienteUseCase.consultarPorTelefone(tel)).thenReturn(Optional.of(cliente));
        when(conversaAgenteUseCase.consultarPorCliente(cliente.getId()))
                .thenReturn(conv);

        LocalDateTime fixed = LocalDateTime.of(2025,8,1,12,0);
        try (MockedStatic<LocalDateTime> mt = mockStatic(LocalDateTime.class)) {
            mt.when(LocalDateTime::now).thenReturn(fixed);
            when(conv.getFinalizada()).thenReturn(true);
            when(conv.getDataUltimaMensagem())
                    .thenReturn(fixed.minusMinutes(40));

            assertTrue(validadorTempoEspera.deveIgnorar(ctx));
            verifyNoInteractions(mensageriaUseCase);
        }
    }
}