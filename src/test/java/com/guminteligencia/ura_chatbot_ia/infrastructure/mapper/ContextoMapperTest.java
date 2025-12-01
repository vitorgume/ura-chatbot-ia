package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ContextoMapperTest {

    private Contexto contextoDomain;
    private ContextoEntity contextoEntity;

    @BeforeEach
    void setUp() {
        List<MensagemContexto> mensagensDomain = List.of(
                MensagemContexto.builder()
                        .mensagem("Mensagem 1")
                        .imagemUrl("img-1")
                        .audioUrl("aud-1")
                        .build(),
                MensagemContexto.builder()
                        .mensagem("Mensagem 2")
                        .imagemUrl("img-2")
                        .audioUrl("aud-2")
                        .build()
        );

        contextoDomain = Contexto.builder()
                .id(UUID.randomUUID())
                .telefone("000000000000")
                .mensagens(mensagensDomain)
                .mensagemFila(Message.builder().build())
                .build();

        List<MensagemContexto> mensagensEntity = List.of(
                MensagemContexto.builder()
                        .mensagem("Mensagem 1")
                        .imagemUrl("img-1")
                        .audioUrl("aud-1")
                        .build(),
                MensagemContexto.builder()
                        .mensagem("Mensagem 2")
                        .imagemUrl("img-2")
                        .audioUrl("aud-2")
                        .build(),
                MensagemContexto.builder()
                        .mensagem("Mensagem 3")
                        .imagemUrl("img-3")
                        .audioUrl("aud-3")
                        .build()
        );

        contextoEntity = ContextoEntity.builder()
                .id(UUID.randomUUID())
                .telefone("000000000001")
                .mensagens(mensagensEntity)
                .build();
    }

    @Test
    void deveTrasnformarParaDomain() {
        Contexto contextoTeste = ContextoMapper.paraDomain(contextoEntity);

        Assertions.assertEquals(contextoTeste.getId(), contextoEntity.getId());
        Assertions.assertEquals(contextoTeste.getTelefone(), contextoEntity.getTelefone());
        Assertions.assertEquals(contextoTeste.getMensagens(), contextoEntity.getMensagens());
        Assertions.assertNull(contextoTeste.getMensagemFila());
    }

    @Test
    void deveTransformarParaEntity() {
        ContextoEntity contextoTeste = ContextoMapper.paraEntity(contextoDomain);

        Assertions.assertEquals(contextoTeste.getId(), contextoDomain.getId());
        Assertions.assertEquals(contextoTeste.getTelefone(), contextoDomain.getTelefone());
        Assertions.assertEquals(contextoTeste.getMensagens(), contextoDomain.getMensagens());
    }

    @Test
    void paraDomainDeMessage_deveMapearCamposEManterMensagemFila() throws Exception {
        UUID expectedId = UUID.randomUUID();
        String expectedTel = "+5511999999999";
        List<MensagemContexto> expectedMsgs = List.of(
                MensagemContexto.builder()
                        .mensagem("oi")
                        .imagemUrl("imgOi")
                        .audioUrl("audOi")
                        .build(),
                MensagemContexto.builder()
                        .mensagem("tchau")
                        .imagemUrl("imgTchau")
                        .audioUrl("audTchau")
                        .build()
        );
        String expectedStatus = "ANDAMENTO";

        String json = String.format(
                """
                {
                  "id":       "%s",
                  "telefone": "%s",
                  "mensagens": %s,
                  "status":   "%s"
                }
                """,
                expectedId,
                expectedTel,
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(expectedMsgs),
                expectedStatus
        );

        Message message = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .receiptHandle("rh-" + UUID.randomUUID())
                .body(json)
                .build();

        Contexto ctx = ContextoMapper.paraDomainDeMessage(message);

        assertAll("Contexto",
                () -> assertEquals(expectedId,   ctx.getId(),            "id deve vir do JSON"),
                () -> assertEquals(expectedTel,  ctx.getTelefone(),      "telefone deve vir do JSON"),
                () -> assertEquals(expectedMsgs, ctx.getMensagens(),     "mensagens deve vir do JSON"),
                () -> assertSame(message,        ctx.getMensagemFila(),  "deve guardar a mesma instância de Message")
        );
    }

    @Test
    void paraDomainDeMessage_jsonInvalido_deveLancarRuntimeException() {
        Message bad = Message.builder()
                .messageId(UUID.randomUUID().toString())
                .receiptHandle("rh-"+UUID.randomUUID())
                .body("isso não é JSON")
                .build();

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                ContextoMapper.paraDomainDeMessage(bad)
        );
        assertTrue(ex.getMessage().contains("Erro ao converter mensagem da fila para Contexto"));
        assertNotNull(ex.getCause());
    }
}
