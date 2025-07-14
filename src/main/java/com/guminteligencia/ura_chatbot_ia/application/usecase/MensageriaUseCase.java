package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensageriaUseCase {

    private final MensageriaGateway mensageriaGateway;

    public List<Contexto> listarContextos() {
        return mensageriaGateway.listarMensagens();
    }

    public void deletarMensagem(Message mensagemFila) {
        mensageriaGateway.deletarMensagem(mensagemFila);
    }
}
