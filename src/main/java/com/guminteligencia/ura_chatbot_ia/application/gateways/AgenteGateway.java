package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemAgenteDto;
import com.guminteligencia.ura_chatbot_ia.domain.RespostaAgente;

public interface AgenteGateway {
    RespostaAgente enviarMensagem(MensagemAgenteDto mensagem);
}
