package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MensagemRecontatoInativoG1DirecionamentoVendedorTest {

    private final MensagemRecontatoInativoG1DirecionamentoVendedor mensagem =
            new MensagemRecontatoInativoG1DirecionamentoVendedor();

    @Test
    void deveRetornarMensagemComNomeDoVendedor() {
        Cliente cliente = Cliente.builder()
                .nome("Fulano")
                .telefone("+55 44 99999-9999")
                .build();

        String resultado = mensagem.getMensagem("Carlos", cliente);

        String esperado = "Perfeito, estou te redirecionando para o vendedor(a) Carlos que logo entrará em contato. Muito obrigado ! Até...";
        assertEquals(esperado, resultado);
    }

    @Test
    void deveRetornarMensagemMesmoComNomeVendedorNull() {
        // Comportamento atual concatena "null" — este teste documenta isso.
        String resultado = mensagem.getMensagem(null, null);
        String esperado = "Perfeito, estou te redirecionando para o vendedor(a) null que logo entrará em contato. Muito obrigado ! Até...";
        assertEquals(esperado, resultado);
    }

    @Test
    void deveRetornarCodigoDoTipoMensagemCorreto() {
        int codigo = mensagem.getTipoMensagem();
        assertEquals(TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR.getCodigo(), codigo);
    }

}