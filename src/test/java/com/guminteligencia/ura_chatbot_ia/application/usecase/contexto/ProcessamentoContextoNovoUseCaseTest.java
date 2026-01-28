package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente.ProcessamentoContextoExistente;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoContextoNovoUseCaseTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @Mock
    private ProcessamentoContextoExistente processamentoContextoExistente;

    @InjectMocks
    private ProcessamentoContextoNovoUseCase useCase;

    @Mock
    private Contexto contexto;

    @Mock
    private Cliente clienteSalvo;

    @Mock
    private ConversaAgente novaConversa;

    private final String telefone = "+5511999000111";

    @Test
    void deveProcessarFluxoCompletoContextoNovo() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(clienteUseCase.consultarPorTelefone(telefone)).thenReturn(Optional.empty());
        when(clienteUseCase.cadastrar(telefone)).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(clienteSalvo)).thenReturn(novaConversa);
        doNothing().when(processamentoContextoExistente).processarContextoExistente(clienteSalvo, contexto);

        useCase.processarContextoNovo(contexto);

        InOrder ord = inOrder(clienteUseCase,
                conversaAgenteUseCase,
                processamentoContextoExistente);

        ord.verify(clienteUseCase).consultarPorTelefone(telefone);
        ord.verify(clienteUseCase).cadastrar(telefone);
        ord.verify(conversaAgenteUseCase).criar(clienteSalvo);
        ord.verify(conversaAgenteUseCase).salvar(novaConversa);
        ord.verify(processamentoContextoExistente).processarContextoExistente(clienteSalvo, contexto);
        verifyNoMoreInteractions(clienteUseCase, conversaAgenteUseCase, processamentoContextoExistente);
    }

    @Test
    void fluxoNaoDeveContinuarAoTerExceptionLancadaAoCadastrarCLiente() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(clienteUseCase.consultarPorTelefone(telefone)).thenReturn(Optional.empty());
        when(clienteUseCase.cadastrar(telefone))
                .thenThrow(new RuntimeException("erro-cadastrar"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-cadastrar", ex.getMessage());

        verify(clienteUseCase).consultarPorTelefone(telefone);
        verify(clienteUseCase).cadastrar(telefone);
        verifyNoMoreInteractions(clienteUseCase);
        verifyNoInteractions(conversaAgenteUseCase, processamentoContextoExistente);
    }

    @Test
    void fluxoNaoDeveContinuarAoTerExceptionLancadaAoCriarConversaAgente() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(clienteUseCase.consultarPorTelefone(telefone)).thenReturn(Optional.empty());
        when(clienteUseCase.cadastrar(telefone)).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(clienteSalvo))
                .thenThrow(new IllegalStateException("erro-criar"));

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-criar", ex.getMessage());

        verify(clienteUseCase).consultarPorTelefone(telefone);
        verify(clienteUseCase).cadastrar(telefone);
        verify(conversaAgenteUseCase).criar(clienteSalvo);
        verifyNoMoreInteractions(conversaAgenteUseCase);
        verifyNoInteractions(processamentoContextoExistente);
    }

    @Test
    void processarContextoNovo_quandoSalvarFalha_propagatesEParaFluxo() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(clienteUseCase.consultarPorTelefone(telefone)).thenReturn(Optional.empty());
        when(clienteUseCase.cadastrar(telefone)).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(clienteSalvo)).thenReturn(novaConversa);
        doThrow(new IllegalArgumentException("erro-msg"))
                .when(conversaAgenteUseCase).salvar(novaConversa);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-msg", ex.getMessage());

        verify(clienteUseCase).consultarPorTelefone(telefone);
        verify(clienteUseCase).cadastrar(telefone);
        verify(conversaAgenteUseCase).criar(clienteSalvo);
        verify(conversaAgenteUseCase).salvar(novaConversa);
        verifyNoMoreInteractions(conversaAgenteUseCase);
        verifyNoInteractions(processamentoContextoExistente);
    }
}
