package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ProcessoContextoExistenteNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarContextoExistenteFactoryTest {

    @Mock
    private ProcessamentoContextoExistenteType procA;

    @Mock
    private ProcessamentoContextoExistenteType procB;

    @Mock
    private ConversaAgente conversaAgente;

    private ProcessarContextoExistenteFactory factory;

    private final String resposta = "alguma resposta";

    @Test
    void deveRetornaPrimeiraInstanciaQuandoValidar() {
        when(procA.deveProcessar(resposta, conversaAgente)).thenReturn(true);

        factory = new ProcessarContextoExistenteFactory(List.of(procA, procB));

        ProcessamentoContextoExistenteType chosen = factory.create(resposta, conversaAgente);

        assertSame(procA, chosen);
        verify(procA).deveProcessar(resposta, conversaAgente);
        verify(procB, never()).deveProcessar(any(), any());
    }

    @Test
    void deveRetornarSegundaInstanciaQuandoPrimeiraNaoValida() {
        when(procA.deveProcessar(resposta, conversaAgente)).thenReturn(false);
        when(procB.deveProcessar(resposta, conversaAgente)).thenReturn(true);

        factory = new ProcessarContextoExistenteFactory(List.of(procA, procB));

        ProcessamentoContextoExistenteType chosen = factory.create(resposta, conversaAgente);

        assertSame(procB, chosen);
        InOrder ord = inOrder(procA, procB);
        ord.verify(procA).deveProcessar(resposta, conversaAgente);
        ord.verify(procB).deveProcessar(resposta, conversaAgente);
    }

    @Test
    void deveRetornaPrimeiraInstanciaQuandoAmbasInstanciasValidam() {
        when(procA.deveProcessar(resposta, conversaAgente)).thenReturn(true);

        factory = new ProcessarContextoExistenteFactory(List.of(procA, procB));

        ProcessamentoContextoExistenteType chosen = factory.create(resposta, conversaAgente);

        assertSame(procA, chosen);

        verify(procB, never()).deveProcessar(any(), any());
    }

    @Test
    void deveRetornaExceptionQuandoNenhumValida() {
        when(procA.deveProcessar(resposta, conversaAgente)).thenReturn(false);
        when(procB.deveProcessar(resposta, conversaAgente)).thenReturn(false);

        factory = new ProcessarContextoExistenteFactory(List.of(procA, procB));

        assertThrows(
                ProcessoContextoExistenteNaoIdentificadoException.class,
                () -> factory.create(resposta, conversaAgente)
        );

        verify(procA).deveProcessar(resposta, conversaAgente);
        verify(procB).deveProcessar(resposta, conversaAgente);
    }
}