package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensagemDirecionamentoVendedorTest {

    private final MensagemDirecionamentoVendedor mensagemDirecionamentoVendedor = new MensagemDirecionamentoVendedor();

    @Test
    void deveIncluirNomeDoVendedorCorretamente() {
        String vendedor = "Maria";

        Cliente cliente = Cliente.builder().nome("João").telefone("+5511999887766").build();

        String msg = mensagemDirecionamentoVendedor.getMensagem(vendedor, cliente);

        String esperado = "Muito obrigado pelas informações ! Agora você será redirecionado para o(a) "
                + vendedor
                + ", logo entrará em contato com você ! Até...";
        assertEquals(esperado, msg);
    }

    @Test
    void deveTratarNomeVendedorNullComStringNull() {
        String msg = mensagemDirecionamentoVendedor.getMensagem(null, null);
        assertTrue(msg.contains("para o(a) null"));
        assertTrue(msg.endsWith("Até..."));
    }

    @Test
    void deveRetornarCoidogCorretamente() {
        int codigo = mensagemDirecionamentoVendedor.getTipoMensagem();
        assertEquals(
                TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo(),
                codigo
        );
    }
}