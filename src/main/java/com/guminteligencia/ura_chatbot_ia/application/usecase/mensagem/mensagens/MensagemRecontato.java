package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRecontato implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Identifiquei que você já estava em conversa com o(a) " + nomeVendedor + ", vou repessar você novamente " +
                "para o vendedor.";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_RECONTATO_VENDEDOR.getCodigo();
    }
}
