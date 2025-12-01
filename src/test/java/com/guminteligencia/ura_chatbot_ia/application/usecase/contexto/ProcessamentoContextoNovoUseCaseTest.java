package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ClienteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

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
    private MensagemUseCase mensagemUseCase;

    @Mock
    private AgenteUseCase agenteUseCase;

    @InjectMocks
    private ProcessamentoContextoNovoUseCase useCase;

    @Mock
    private Contexto contexto;

    @Mock
    private Cliente clienteSalvo;

    @Mock
    private ConversaAgente novaConversa;

    private final String telefone = "+5511999000111";
    private final List<MensagemContexto> mensagens = List.of(
            MensagemContexto.builder().mensagem("oi").build()
    );

    @Test
    void deveProcessarFluxoCompletoContextoNovo() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(contexto.getMensagens()).thenReturn(mensagens);
        when(clienteUseCase.cadastrar(telefone)).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(clienteSalvo)).thenReturn(novaConversa);
        when(agenteUseCase.enviarMensagem(clienteSalvo, novaConversa, mensagens))
                .thenReturn("resposta");
        when(clienteSalvo.getTelefone()).thenReturn(telefone);

        useCase.processarContextoNovo(contexto);

        InOrder ord = inOrder(clienteUseCase,
                conversaAgenteUseCase,
                agenteUseCase,
                mensagemUseCase,
                novaConversa,
                conversaAgenteUseCase);

        ord.verify(clienteUseCase).cadastrar(telefone);
        ord.verify(conversaAgenteUseCase).criar(clienteSalvo);
        ord.verify(agenteUseCase).enviarMensagem(clienteSalvo, novaConversa, mensagens);
        ord.verify(mensagemUseCase).enviarMensagem("resposta", telefone, true);
        ord.verify(novaConversa).setDataUltimaMensagem(Mockito.any(LocalDateTime.class));
        ord.verify(conversaAgenteUseCase).salvar(novaConversa);
        ord.verifyNoMoreInteractions();
    }

    @Test
    void fluxoNaoDeveContinuarAoTerExceptionLancadaAoCadastrarCLiente() {
        when(clienteUseCase.cadastrar(Mockito.anyString()))
                .thenThrow(new RuntimeException("erro-cadastrar"));

        when(contexto.getTelefone()).thenReturn(telefone);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-cadastrar", ex.getMessage());

        verify(clienteUseCase).cadastrar(telefone);
        verifyNoInteractions(conversaAgenteUseCase, agenteUseCase, mensagemUseCase);
    }

    @Test
    void fluxoNaoDeveContinuarAoTerExceptionLancadaAoCriarConversaAgente() {
        when(clienteUseCase.cadastrar(Mockito.anyString())).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(Mockito.any()))
                .thenThrow(new IllegalStateException("erro-criar"));
        when(contexto.getTelefone()).thenReturn(telefone);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-criar", ex.getMessage());

        verify(clienteUseCase).cadastrar(telefone);
        verify(conversaAgenteUseCase).criar(clienteSalvo);
        verifyNoInteractions(agenteUseCase, mensagemUseCase);
    }

    @Test
    void fluxoNaoDeveContinuarAoTerExceotionLancadaNoEnvioMensagemAgenteUseCase() {
        when(clienteUseCase.cadastrar(Mockito.anyString())).thenReturn(clienteSalvo);
        when(conversaAgenteUseCase.criar(Mockito.any())).thenReturn(novaConversa);
        when(agenteUseCase.enviarMensagem(any(), any(), anyList()))
                .thenThrow(new RuntimeException("erro-agente"));
        when(contexto.getTelefone()).thenReturn(telefone);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-agente", ex.getMessage());

        verify(clienteUseCase).cadastrar(telefone);
        verify(conversaAgenteUseCase).criar(clienteSalvo);
        verify(agenteUseCase, never()).enviarMensagem(clienteSalvo, novaConversa, mensagens);
        verifyNoInteractions(mensagemUseCase);
    }

    @Test
    void processarContextoNovo_quandoSalvarFalha_propagatesEParaFluxo() {
        when(contexto.getTelefone()).thenReturn(telefone);
        when(contexto.getMensagens()).thenReturn(mensagens);
        when(clienteUseCase.cadastrar(telefone)).thenReturn(clienteSalvo);
        when(clienteSalvo.getTelefone()).thenReturn(telefone);
        when(conversaAgenteUseCase.criar(clienteSalvo)).thenReturn(novaConversa);
        when(agenteUseCase.enviarMensagem(clienteSalvo, novaConversa, mensagens))
                .thenReturn("resposta");

        doThrow(new IllegalArgumentException("erro-msg"))
                .when(conversaAgenteUseCase).salvar(any());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> useCase.processarContextoNovo(contexto)
        );
        assertEquals("erro-msg", ex.getMessage());

        verify(clienteUseCase).cadastrar(telefone);
        verify(conversaAgenteUseCase).criar(clienteSalvo);
        verify(agenteUseCase).enviarMensagem(clienteSalvo, novaConversa, mensagens);
        verify(mensagemUseCase).enviarMensagem(eq("resposta"), eq(telefone), eq(true));
        verify(novaConversa).setDataUltimaMensagem(any());
        verify(conversaAgenteUseCase).salvar(any());
        verifyNoMoreInteractions(clienteUseCase, conversaAgenteUseCase, agenteUseCase, mensagemUseCase, novaConversa);
    }


}
