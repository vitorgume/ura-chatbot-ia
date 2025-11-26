package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.AvisoContexto;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AvisoContextoMapperTest {

    @Test
    void deveConverterMensagemJsonDireta() {
        UUID idContexto = UUID.randomUUID();
        String body = """
                {"idContexto":"%s"}
                """.formatted(idContexto);
        Message mensagem = Message.builder().body(body).build();

        AvisoContexto aviso = AvisoContextoMapper.paraDomainDeMessage(mensagem);

        assertEquals(idContexto, aviso.getIdContexto());
        assertEquals(mensagem, aviso.getMensagemFila());
    }

    @Test
    void deveConverterEnvelopeSnsComCampoMessage() {
        UUID idContexto = UUID.randomUUID();
        String innerJson = "{\"idContexto\":\"%s\"}".formatted(idContexto);
        String envelope = "{\"Type\":\"Notification\",\"Message\":\"%s\"}"
                .formatted(innerJson.replace("\"", "\\\""));
        Message mensagem = Message.builder().body(envelope).build();

        AvisoContexto aviso = AvisoContextoMapper.paraDomainDeMessage(mensagem);

        assertEquals(idContexto, aviso.getIdContexto());
        assertEquals(mensagem, aviso.getMensagemFila());
    }
}
