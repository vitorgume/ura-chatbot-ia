package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensageriaUseCaseTest {

    @Mock
    private MensageriaGateway mensageriaGateway;

    @InjectMocks
    private MensageriaUseCase useCase;

    @Mock
    private Message mensagemFila;

    @Test
    void deveListarContextosDelegandoParaGateway() {
        Contexto c1 = mock(Contexto.class);
        List<Contexto> lista = List.of(c1);
        when(mensageriaGateway.listarMensagens()).thenReturn(lista);

        List<Contexto> result = useCase.listarContextos();

        assertSame(lista, result, "Deve retornar exatamente a lista fornecida pelo gateway");
        verify(mensageriaGateway).listarMensagens();
        verifyNoMoreInteractions(mensageriaGateway);
    }

    @Test
    void deveDeletarMensagemDelegandoParaGateway() {
        useCase.deletarMensagem(mensagemFila);

        verify(mensageriaGateway).deletarMensagem(mensagemFila);
        verifyNoMoreInteractions(mensageriaGateway);
    }
}