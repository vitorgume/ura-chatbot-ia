package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.SqsGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqsUseCase {

    private final SqsGateway sqsGateway;

    public List<Contexto> listarContextos() {
        return sqsGateway.listarContextos();
    }

    public void deletarMensagem(Message mensagemFila) {
        sqsGateway.deletarMensagem(mensagemFila);
    }
}
