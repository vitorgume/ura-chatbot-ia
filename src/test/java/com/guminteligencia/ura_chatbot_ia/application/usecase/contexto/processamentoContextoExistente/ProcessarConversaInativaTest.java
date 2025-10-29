package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.application.usecase.CrmUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.TipoInativo;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessarConversaInativaTest {

    @Mock
    private VendedorUseCase vendedorUseCase;

    @Mock
    private CrmUseCase crmUseCase;

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private MensagemBuilder mensagemBuilder;

    private ProcessarConversaInativa processador;

    @BeforeEach
    void setup() {
        processador = new ProcessarConversaInativa(vendedorUseCase, crmUseCase, mensagemUseCase, mensagemBuilder);
    }

    @Test
    void deveProcessarFluxoCompletoComSucesso() {
        // Mocks de entidades (para verificar setters/gets)
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5511999999999");
        when(vendedor.getNome()).thenReturn("Vendedor Teste");

        // Roleta vendedor
        when(vendedorUseCase.roletaVendedoresConversaInativa(cliente)).thenReturn(vendedor);

        // Mensagem do builder
        when(mensagemBuilder.getMensagem(
                eq(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR),
                eq("Vendedor Teste"),
                isNull()
        )).thenReturn("mensagem-g1-direcionamento");

        // Execução
        processador.processar("qualquer-resposta", conversa, cliente);

        // Ordem esperada do fluxo
        InOrder inOrder = inOrder(conversa, vendedorUseCase, mensagemBuilder, mensagemUseCase, crmUseCase);

        inOrder.verify(conversa).setFinalizada(true);
        inOrder.verify(vendedorUseCase).roletaVendedoresConversaInativa(cliente);
        inOrder.verify(conversa).setVendedor(vendedor);

        inOrder.verify(mensagemBuilder).getMensagem(
                eq(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR),
                eq("Vendedor Teste"),
                isNull()
        );
        inOrder.verify(mensagemUseCase).enviarMensagem(eq("mensagem-g1-direcionamento"), eq("+5511999999999"), eq(false));

        inOrder.verify(crmUseCase).atualizarCrm(eq(vendedor), eq(cliente), eq(conversa));
        inOrder.verify(mensagemUseCase).enviarContatoVendedor(eq(vendedor), eq(cliente));
        inOrder.verifyNoMoreInteractions();

        // Nenhuma outra interação inesperada
        verifyNoMoreInteractions(vendedorUseCase, mensagemBuilder, mensagemUseCase, crmUseCase, conversa, cliente, vendedor);
    }

    @Test
    void devePropagarExcecaoQuandoFalhaAoSortearVendedor() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);

        when(vendedorUseCase.roletaVendedoresConversaInativa(cliente))
                .thenThrow(new IllegalStateException("erro-roleta"));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> processador.processar("resp", conversa, cliente));
        assertEquals("erro-roleta", ex.getMessage());

        // Garantir que nada prossegue após a falha
        verify(conversa).setFinalizada(true); // foi chamado antes da roleta
        verifyNoInteractions(mensagemBuilder);
        verifyNoInteractions(crmUseCase);
        verifyNoMoreInteractions(mensagemUseCase);
    }

    @Test
    void devePropagarExcecaoQuandoFalhaAoEnviarMensagemInicial() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5544999999999");
        when(vendedor.getNome()).thenReturn("João");
        when(vendedorUseCase.roletaVendedoresConversaInativa(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(
                eq(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR),
                eq("João"), isNull()))
                .thenReturn("msg");
        doThrow(new RuntimeException("erro-envio"))
                .when(mensagemUseCase).enviarMensagem(eq("msg"), eq("+5544999999999"), eq(false));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processador.processar("resp", conversa, cliente));
        assertEquals("erro-envio", ex.getMessage());

        // Já houve setFinalizada e setVendedor, mas não deve chamar CRM/contato vendedor
        verify(conversa).setFinalizada(true);
        verify(conversa).setVendedor(vendedor);
        verifyNoInteractions(crmUseCase);
        verify(mensagemUseCase, never()).enviarContatoVendedor(any(), any());
    }

    @Test
    void devePropagarExcecaoQuandoFalhaAoAtualizarCrm() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5533999999999");
        when(vendedor.getNome()).thenReturn("Maria");
        when(vendedorUseCase.roletaVendedoresConversaInativa(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(
                eq(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR),
                eq("Maria"), isNull()))
                .thenReturn("msg-ok");

        // Envio de mensagem ok
        doNothing().when(mensagemUseCase).enviarMensagem("msg-ok", "+5533999999999", false);
        // CRM falha
        doThrow(new IllegalArgumentException("erro-crm"))
                .when(crmUseCase).atualizarCrm(vendedor, cliente, conversa);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> processador.processar("resp", conversa, cliente));
        assertEquals("erro-crm", ex.getMessage());

        // Não deve enviar contato do vendedor após falha no CRM
        verify(mensagemUseCase, never()).enviarContatoVendedor(any(), any());
    }

    @Test
    void devePropagarExcecaoQuandoFalhaAoEnviarContatoDoVendedor() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        Cliente cliente = mock(Cliente.class);
        Vendedor vendedor = mock(Vendedor.class);

        when(cliente.getTelefone()).thenReturn("+5577999999999");
        when(vendedor.getNome()).thenReturn("Ana");
        when(vendedorUseCase.roletaVendedoresConversaInativa(cliente)).thenReturn(vendedor);
        when(mensagemBuilder.getMensagem(
                eq(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR),
                eq("Ana"), isNull()))
                .thenReturn("msg-ok");
        doNothing().when(mensagemUseCase).enviarMensagem("msg-ok", "+5577999999999", false);
        doNothing().when(crmUseCase).atualizarCrm(vendedor, cliente, conversa);

        doThrow(new RuntimeException("erro-contato"))
                .when(mensagemUseCase).enviarContatoVendedor(vendedor, cliente);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> processador.processar("resp", conversa, cliente));
        assertEquals("erro-contato", ex.getMessage());
    }

    // ===========================
    //  Testes de deveProcessar()
    // ===========================

    @Test
    void deveProcessar_quandoInativoNaoNull_eCodigoIgualZero() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        TipoInativo inativo = mock(TipoInativo.class);
        when(conversa.getInativo()).thenReturn(inativo);
        when(inativo.getCodigo()).thenReturn(0);

        boolean result = processador.deveProcessar("qualquer", conversa);
        assertTrue(result);
    }

    @Test
    void naoDeveProcessar_quandoInativoNull() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        when(conversa.getInativo()).thenReturn(null);

        boolean result = processador.deveProcessar("qualquer", conversa);
        assertFalse(result);
    }

    @Test
    void naoDeveProcessar_quandoInativoNaoNull_eCodigoDiferenteDeZero() {
        ConversaAgente conversa = mock(ConversaAgente.class);
        TipoInativo inativo = mock(TipoInativo.class);
        when(conversa.getInativo()).thenReturn(inativo);
        when(inativo.getCodigo()).thenReturn(1);

        boolean result = processador.deveProcessar("qualquer", conversa);
        assertFalse(result);
    }
}