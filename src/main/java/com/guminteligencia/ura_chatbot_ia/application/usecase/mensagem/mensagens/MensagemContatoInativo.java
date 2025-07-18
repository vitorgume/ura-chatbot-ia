package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemContatoInativo implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Contato inativo por mais de 30 minutos";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.CONTATO_INATIVO.getCodigo();
    }
}
