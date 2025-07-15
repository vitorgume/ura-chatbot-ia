package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensageriaUseCase {

    private final MensageriaGateway mensageriaGateway;

    public List<Contexto> listarContextos() {
        log.info("Listando contextos da fila.");

        List<Contexto> contextos = mensageriaGateway.listarMensagens();

        log.info("Contextos listados com sucesso.");

        return contextos;
    }

    public void deletarMensagem(Message mensagemFila) {
        log.info("Deletando mensagem da fila. Mensagem: {}", mensagemFila);

        mensageriaGateway.deletarMensagem(mensagemFila);

        log.info("Mensagem deletada com sucesso da fila.");
    }
}
