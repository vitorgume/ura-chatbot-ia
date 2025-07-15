package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MensagemBuilder {

    private final MensagemFactory mensagemFactory;

    public String getMensagem(TipoMensagem tipoMensagem, String nomeVendedor, Cliente cliente) {
        return mensagemFactory.create(tipoMensagem).getMensagem(nomeVendedor, cliente);
    }
}
