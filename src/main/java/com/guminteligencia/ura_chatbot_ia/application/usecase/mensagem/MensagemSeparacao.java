package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemSeparacao implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "✳️✳️✳️✳️✳️✳️";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_SEPARACAO.getCodigo();
    }
}
