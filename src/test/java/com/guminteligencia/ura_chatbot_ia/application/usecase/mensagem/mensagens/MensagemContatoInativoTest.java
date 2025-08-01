package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MensagemContatoInativoTest {

    private final MensagemContatoInativo mensagemContatoInativo = new MensagemContatoInativo();

    @Test
    void deveRetornaTextoComSucesso() {
        String nomeVendedor = "Qualquer";

        Cliente cliente = Cliente.builder().nome("ClienteX").telefone("+5511999000111").build();

        String resultado = mensagemContatoInativo.getMensagem(nomeVendedor, cliente);

        assertEquals(
                "Contato inativo por mais de 30 minutos",
                resultado,
                "Deve retornar a mensagem fixa de contato inativo"
        );
    }

    @Test
    void deveRetornaMensagemComParametroNull() {
        String resultado = mensagemContatoInativo.getMensagem(null, null);
        assertEquals(
                "Contato inativo por mais de 30 minutos",
                resultado
        );
    }

    @Test
    void deveRetornaCodigoCorretoParaValidacao() {
        int codigo = mensagemContatoInativo.getTipoMensagem();
        assertEquals(
                TipoMensagem.CONTATO_INATIVO.getCodigo(),
                codigo,
                "Deve retornar o código correspondente a CONTATO_INATIVO"
        );
    }

}