package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensagemRecontatoTest {
    private final MensagemRecontato mensagemRecontato = new MensagemRecontato();

    @Test
    void deveRetornarMensagemComNomeVendedor() {
        String vendedor = "Ana";
        Cliente cliente = Cliente.builder().nome("Carlos").telefone("+5511999000111").build();

        String msg = mensagemRecontato.getMensagem(vendedor, cliente);

        String esperado = "Identifiquei que você já estava em conversa com o(a) Ana, "
                + "vou repassar você novamente para o vendedor.";
        assertEquals(esperado, msg);
    }

    @Test
    void deveRetornarMensagemComoStringNullNomeVendedorNull() {
        String msg = mensagemRecontato.getMensagem(null, null);
        assertTrue(msg.startsWith("Identifiquei que você já estava em conversa com o(a) null"));
        assertTrue(msg.endsWith("para o vendedor."));
    }

    @Test
    void deveRetornarCodigoCorreto() {
        int codigo = mensagemRecontato.getTipoMensagem();
        assertEquals(
                TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR.getCodigo(),
                codigo
        );
    }

}