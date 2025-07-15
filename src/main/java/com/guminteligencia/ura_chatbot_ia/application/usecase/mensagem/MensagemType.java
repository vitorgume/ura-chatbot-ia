package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;

public interface MensagemType {
    String getMensagem(String nomeVendedor, Cliente cliente);
    Integer getTipoMensagem();
}
