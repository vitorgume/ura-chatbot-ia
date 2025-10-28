package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import org.springframework.stereotype.Component;

@Component
public class ProcessarConversaInativa implements ProcessamentoContextoExistenteType {

    @Override
    public void processar(String resposta, ConversaAgente conversaAgente, Cliente cliente) {

    }

    @Override
    public boolean deveProcessar(String resposta, ConversaAgente conversaAgente) {
        return false;
    }
}
