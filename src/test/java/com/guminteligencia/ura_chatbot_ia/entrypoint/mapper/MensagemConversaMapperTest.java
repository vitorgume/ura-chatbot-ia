package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MensagemConversaDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.UUID;

class MensagemConversaMapperTest {

    private MensagemConversa mensagemConversaDomain;

    @BeforeEach
    void setUp() {
        mensagemConversaDomain = MensagemConversa.builder()
                .id(UUID.randomUUID())
                .responsavel("usuario")
                .conteudo("teste 123")
                .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                .conversaAgente(ConversaAgente.builder()
                        .id(UUID.randomUUID())
                        .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                        .vendedor(Vendedor.builder().id(1L).build())
                        .build()
                )
                .build();
    }

    @Test
    void deveRetornarDtoComSucesso() {
        MensagemConversaDto resultado = MensagemConversaMapper.paraDto(mensagemConversaDomain);

        Assertions.assertEquals(mensagemConversaDomain.getId(), resultado.getId());
        Assertions.assertEquals(mensagemConversaDomain.getResponsavel(), resultado.getResponsavel());
        Assertions.assertEquals(mensagemConversaDomain.getConteudo(), resultado.getConteudo());
        Assertions.assertEquals(mensagemConversaDomain.getData(), resultado.getData());
    }
}