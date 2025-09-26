package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.validador;

import com.guminteligencia.ura_chatbot_ia.application.usecase.MensageriaUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.ContextoUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.StatusContexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidadorStatusTest {

    @Mock
    ContextoUseCase contextoUseCase;

    @Mock
    MensageriaUseCase mensageriaUseCase;

    @InjectMocks
    ValidadorStatus sut;

    @Test
    void deveRetornaFalaseStatus0DeletarMensagemRetornar() {
        UUID id = UUID.randomUUID();
        Contexto input = mock(Contexto.class);
        when(input.getId()).thenReturn(id);
        Message msgFila = Message.builder().build();

        Contexto salvo = mock(Contexto.class);
        StatusContexto st = mock(StatusContexto.class);
        when(st.getCodigo()).thenReturn(0);
        when(salvo.getStatus()).thenReturn(st);
        when(contextoUseCase.consultarPeloId(id)).thenReturn(salvo);

        boolean result = sut.permitirProcessar(input);
        assertFalse(result);
    }

    @Test
    void deveRetornaTrueStatusNao1NaoDeletar() {
        UUID id = UUID.randomUUID();
        Contexto input = mock(Contexto.class);
        when(input.getId()).thenReturn(id);

        Contexto salvo = mock(Contexto.class);
        StatusContexto st = mock(StatusContexto.class);
        when(st.getCodigo()).thenReturn(2);
        when(salvo.getStatus()).thenReturn(st);
        when(contextoUseCase.consultarPeloId(id)).thenReturn(salvo);

        boolean result = sut.permitirProcessar(input);
        assertTrue(result);
        verifyNoInteractions(mensageriaUseCase);
    }
}