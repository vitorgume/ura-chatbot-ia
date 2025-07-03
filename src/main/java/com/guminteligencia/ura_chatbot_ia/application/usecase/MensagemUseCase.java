package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.SqsGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MensagemUseCase {

    private final SqsUseCase sqsUseCase;
    private final ContextoUseCase contextoUseCase;

    @Scheduled(fixedDelay = 5000)
    public void consumirMensagens() {
        List<Contexto> contextos = sqsUseCase.listarContextos();

        contextos.forEach(contexto -> {
            this.processarMensagem(contexto);

            sqsUseCase.deletarMensagem(contexto.getMensagemFila());

            contextoUseCase.deletar(contexto.getId());
        });
    }

    private void processarMensagem(Contexto contexto) {


    }
}
