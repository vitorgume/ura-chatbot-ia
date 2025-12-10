package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ConversaAgenteUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.OutroContatoUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessamentoRecontatoTest {

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private MensagemBuilder mensagemBuilder;

    @Mock
    private OutroContatoUseCase outroContatoUseCase;

    @Mock
    private ConversaAgenteUseCase conversaAgenteUseCase;

    @InjectMocks
    private ProcessamentoRecontato processamentoRecontato;

    @Mock
    private ConversaAgente conversaAgente;

    @Mock
    private Cliente cliente;

    @Mock
    private Vendedor vendedor;

    @Mock
    private OutroContato outroContato;

    private final String resposta = "resposta-padrao";
    private final String telCliente = "+5511999999999";
    private final String telOutro   = "+5511888888888";

    @BeforeEach
    void init() {
        vendedor = Vendedor.builder().id(1L).nome("Carlos").build();
        cliente = Cliente.builder().id(UUID.randomUUID()).telefone(telCliente).build();
    }

    @Test
    void deveRetornarTrueQuandoConversaForFinalizada() {
        when(conversaAgente.getFinalizada()).thenReturn(true);
        assertTrue(processamentoRecontato.deveProcessar("qualquer", conversaAgente));
    }

    @Test
    void deveRetornarFalseQuandoConversaNaoForFinalizada() {
        when(conversaAgente.getFinalizada()).thenReturn(false);
        assertFalse(processamentoRecontato.deveProcessar("qualquer", conversaAgente));
    }

    @Test
    void deveProcessarFluxoCompletoQuandoRecontatoForFalse() {
        when(conversaAgente.getVendedor()).thenReturn(vendedor);

        when(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR, "Carlos", null))
                .thenReturn("msg-recontato-vendedor");
        when(mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_ALERTA_RECONTATO, "Carlos", cliente))
                .thenReturn("msg-alerta-recontato");
        when(outroContatoUseCase.consultarPorNome("Ana")).thenReturn(outroContato);
        when(outroContato.getTelefone()).thenReturn(telOutro);

        processamentoRecontato.processar(resposta, conversaAgente, cliente);

        InOrder ord = inOrder(mensagemUseCase, outroContatoUseCase, conversaAgente, conversaAgenteUseCase);

        ord.verify(mensagemUseCase).enviarMensagem("msg-recontato-vendedor", telCliente, false);

        ord.verify(outroContatoUseCase).consultarPorNome("Ana");

        ord.verify(mensagemUseCase).enviarMensagem("msg-alerta-recontato", telOutro, false);

        ord.verify(conversaAgente).setRecontato(true);
        ord.verify(conversaAgenteUseCase).salvar(conversaAgente);
        ord.verifyNoMoreInteractions();
    }

    @Test
    void deveProcessarQuandoOutroContatoFalha() {
        when(conversaAgente.getRecontato()).thenReturn(false);
        when(conversaAgente.getVendedor()).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(any(), any(), any())).thenReturn("m");
        when(outroContatoUseCase.consultarPorNome("Ana"))
                .thenThrow(new RuntimeException("fail-outro"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processamentoRecontato.processar(resposta, conversaAgente, cliente));
        assertEquals("fail-outro", ex.getMessage());
        verify(conversaAgente, never()).setRecontato(true);
        verify(conversaAgenteUseCase, never()).salvar(any());
    }

}