package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

public interface MensageriaGateway {
    List<AvisoContexto> listarAvisos();

    void deletarMensagem(Message id);
}
