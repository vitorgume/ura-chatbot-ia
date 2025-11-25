package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.AvisoContextoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ContextoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;

@Component
@Slf4j
public class MensageriaDataProvider implements MensageriaGateway {

    private final String MENSAGEM_ERRO_DELETAR_MENSAGEM_FILA = "Erro ao deletar mensagem da fila SQS";
    private final String MENSAGEM_ERRO_LISTAR_CONTEXTOS_SQS = "Erro ao listar contextos da fila SQS.";
    private final SqsClient sqsClient;

    @Value("${aws.sqs.url}")
    private final String queueUrl;

    public MensageriaDataProvider(
            SqsClient sqsClient,
            @Value("${aws.sqs.url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
    }


    @Override
    public List<AvisoContexto> listarAvisos() {

        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            return messages.stream()
                    .map(AvisoContextoMapper::paraDomainDeMessage)
                    .toList();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_CONTEXTOS_SQS, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_CONTEXTOS_SQS, ex.getCause());
        }
    }

    @Override
    public void deletarMensagem(Message message) {

        try {
            sqsClient.deleteMessage(DeleteMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .receiptHandle(message.receiptHandle())
                    .build());
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_MENSAGEM_FILA, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_MENSAGEM_FILA, ex.getCause());
        }
    }
}
