package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ContextoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MensageriaDataProviderTest {

    @Mock
    private SqsClient sqsClient;

    private MensageriaDataProvider provider;
    private final String queueUrl = "https://sqs.test/queue";

    @BeforeEach
    void setup() {
        provider = new MensageriaDataProvider(sqsClient, queueUrl);
    }

//    @Test
//    void deveListarAvisosComSucesso() {
//        Message msg1 = Message.builder()
//                .receiptHandle("rh1")
//                .body("b1")
//                .messageId("id1")
//                .build();
//        Message msg2 = Message.builder()
//                .receiptHandle("rh2")
//                .body("b2")
//                .messageId("id2")
//                .build();
//        ReceiveMessageResponse response =
//                ReceiveMessageResponse.builder()
//                        .messages(msg1, msg2)
//                        .build();
//
//        ArgumentCaptor<ReceiveMessageRequest> reqCap =
//                ArgumentCaptor.forClass(ReceiveMessageRequest.class);
//        when(sqsClient.receiveMessage(reqCap.capture()))
//                .thenReturn(response);
//
//        Contexto ctx1 = mock(Contexto.class);
//        Contexto ctx2 = mock(Contexto.class);
//        try (MockedStatic<ContextoMapper> ms = mockStatic(ContextoMapper.class)) {
//            ms.when(() -> ContextoMapper.paraDomainDeMessage(msg1)).thenReturn(ctx1);
//            ms.when(() -> ContextoMapper.paraDomainDeMessage(msg2)).thenReturn(ctx2);
//
//            List<Contexto> result = provider.listarAvisos();
//
//            assertEquals(List.of(ctx1, ctx2), result);
//
//            ReceiveMessageRequest actualReq = reqCap.getValue();
//            assertEquals(queueUrl, actualReq.queueUrl());
//            assertEquals(10, actualReq.maxNumberOfMessages());
//            assertEquals(5, actualReq.waitTimeSeconds());
//
//            ms.verify(() -> ContextoMapper.paraDomainDeMessage(msg1));
//            ms.verify(() -> ContextoMapper.paraDomainDeMessage(msg2));
//        }
//    }

    @Test
    void deveLancarExceptionAoListarAvisos() {
        when(sqsClient.receiveMessage(any(ReceiveMessageRequest.class)))
                .thenThrow(new RuntimeException("fail-list"));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.listarAvisos()
        );
        assertEquals("Erro ao listar contextos da fila SQS.", ex.getMessage());
    }

    @Test
    void deveDeletarMensagemComSucesso() {
        Message msg = Message.builder()
                .receiptHandle("rh-delete")
                .messageId("idDel")
                .build();

        provider.deletarMensagem(msg);

        ArgumentCaptor<DeleteMessageRequest> cap =
                ArgumentCaptor.forClass(DeleteMessageRequest.class);
        verify(sqsClient).deleteMessage(cap.capture());

        DeleteMessageRequest dr = cap.getValue();
        assertEquals(queueUrl, dr.queueUrl());
        assertEquals("rh-delete", dr.receiptHandle());
    }

    @Test
    void deveLancarExceptionAoDeletarMensagem() {
        Message msg = Message.builder()
                .receiptHandle("rhX")
                .messageId("idX")
                .build();
        doThrow(new RuntimeException("fail-del-msg"))
                .when(sqsClient).deleteMessage(any(DeleteMessageRequest.class));

        DataProviderException ex = assertThrows(
                DataProviderException.class,
                () -> provider.deletarMensagem(msg)
        );
        assertEquals("Erro ao deletar mensagem da fila SQS", ex.getMessage());
    }
}