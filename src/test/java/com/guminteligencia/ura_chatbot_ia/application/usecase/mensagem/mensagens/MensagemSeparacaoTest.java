package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MensagemSeparacaoTest {

    private MensagemSeparacao mensagemSeparacao = new MensagemSeparacao();

    @Test
    void deveRetornarMensagemComSucesso() {
        String mensagem = mensagemSeparacao.getMensagem("", null);

        assertEquals("✳️✳️✳️✳️✳️✳️", mensagem);
    }

    @Test
    void deveRetornarCodigoCorreto() {
        int codigo = mensagemSeparacao.getTipoMensagem();
        assertEquals(TipoMensagem.MENSAGEM_SEPARACAO.getCodigo(), codigo);
    }
}