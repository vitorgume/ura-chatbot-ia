package com.guminteligencia.ura_chatbot_ia.application;

import com.guminteligencia.ura_chatbot_ia.application.gateways.SqsGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SqsUseCase {

    private final SqsGateway sqsGateway;

    @Scheduled(fixedDelay = 5000)
    public void consumirMensagens() {
        List<Contexto> contextos = sqsGateway.listarContextos();




        for (Message message : messages) {
            try {
                Contexto contexto = objectMapper.readValue(message.body(), Contexto.class);

                // Processa a mensagem aqui

                // Deleta a mensagem após o processamento
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());

            } catch (Exception e) {
                // Log e tratamento de erro
                System.err.println("Erro ao processar mensagem: " + e.getMessage());
            }
        }
    }
}
