package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.gateways.SqsGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ContextoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SqsDataProvider implements SqsGateway {

    private final String MENSAGEM_ERRO_DELETAR_MENSAGEM_FILA = "Erro ao deletar mensagem da fila SQS";
    private final String MENSAGEM_ERRO_LISTAR_CONTEXTOS_SQS = "Erro ao listar contextos da fila SQS.";
    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper;

    @Value("${aws.sqs.url}")
    private final String queueUrl;

    public SqsDataProvider(
            SqsClient sqsClient,
            ObjectMapper objectMapper,
            @Value("${aws.sqs.url}") String queueUrl
    ) {
        this.sqsClient = sqsClient;
        this.objectMapper = objectMapper;
        this.queueUrl = queueUrl;
    }


    @Override
    public List<Contexto> listarContextos() {

        try {
            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(5)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            return messages.stream()
                    .map(ContextoMapper::paraDomainDeMessage)
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
