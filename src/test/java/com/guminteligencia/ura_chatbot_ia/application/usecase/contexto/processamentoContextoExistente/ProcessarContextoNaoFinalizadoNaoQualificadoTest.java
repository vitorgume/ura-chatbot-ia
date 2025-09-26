package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarContextoNaoFinalizadoNaoQualificadoTest {

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private ConversaAgente conversaAgente;

    @Mock
    private Cliente cliente;

    @InjectMocks
    private ProcessarContextoNaoFinalizadoNaoQualificado processarContextoNaoFinalizadoNaoQualificado;

    private final String resposta = "alguma mensagem";
    private final String telefone = "+5511999887766";

    @Test
    void deveRetornarTrueQuandoConversaNaoFinalizada() {
        when(conversaAgente.getFinalizada()).thenReturn(false);

        assertTrue(processarContextoNaoFinalizadoNaoQualificado.deveProcessar(resposta, conversaAgente));

        verify(conversaAgente).getFinalizada();
        verifyNoMoreInteractions(conversaAgente);
    }

    @Test
    void deveRetornarFalseQuandoConversaFinalizada() {
        when(conversaAgente.getFinalizada()).thenReturn(true);

        assertFalse(processarContextoNaoFinalizadoNaoQualificado.deveProcessar(resposta, conversaAgente));

        verify(conversaAgente).getFinalizada();
        verifyNoMoreInteractions(conversaAgente);
    }

    @Test
    void deveProcessarFluxoCompleto() {
        when(conversaAgente.getCliente()).thenReturn(cliente);
        when(cliente.getTelefone()).thenReturn(telefone);

        processarContextoNaoFinalizadoNaoQualificado.processar(resposta, conversaAgente, cliente);

        verify(conversaAgente).getCliente();
        verify(cliente).getTelefone();
        verify(mensagemUseCase).enviarMensagem(resposta, telefone, true);

        verifyNoMoreInteractions(conversaAgente, cliente, mensagemUseCase);
    }

    @Test
    void deveProcessarFluxoCompletoComTelefoneNull() {
        when(conversaAgente.getCliente()).thenReturn(cliente);
        when(cliente.getTelefone()).thenReturn(null);

        processarContextoNaoFinalizadoNaoQualificado.processar(resposta, conversaAgente, cliente);

        verify(conversaAgente).getCliente();
        verify(cliente).getTelefone();
        verify(mensagemUseCase).enviarMensagem(resposta, (String) null, true);
        verifyNoMoreInteractions(mensagemUseCase);
    }
}