package com.guminteligencia.ura_chatbot_ia.application.usecase.contexto.processamentoContextoExistente;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;

public interface ProcessamentoContextoExistenteType {
    void processar(RespostaAgente resposta, ConversaAgente conversaAgente, Cliente cliente);
    boolean deveProcessar(RespostaAgente resposta, ConversaAgente conversaAgente);
}
