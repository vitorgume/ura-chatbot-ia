package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MensagemRecontatoIantivoG1Test {
    private final MensagemRecontatoIantivoG1 mensagem = new MensagemRecontatoIantivoG1();

    @Test
    void deveRetornarMensagemExataIndependenteDosParametros() {
        Cliente cliente = Cliente.builder()
                .nome("Fulano")
                .telefone("+55 44 99999-9999")
                .build();

        String esperado = "Oii! Aqui é da Gráfica Neoprint! Você nos enviou mensagem, mas não deu continuidade no atendimento! Se você quiser que eu te conecte com um vendedor, é só me responder essa mensagem, ok?";

        // Mesmo passando vendedor/cliente, a mensagem não usa esses dados
        String resultado1 = mensagem.getMensagem("Carlos", cliente);
        String resultado2 = mensagem.getMensagem(null, null);

        assertEquals(esperado, resultado1);
        assertEquals(esperado, resultado2);
    }

    @Test
    void deveRetornarCodigoDoTipoMensagemCorreto() {
        int codigo = mensagem.getTipoMensagem();
        assertEquals(TipoMensagem.RECONTATO_INATIVO_G1.getCodigo(), codigo);
    }

}