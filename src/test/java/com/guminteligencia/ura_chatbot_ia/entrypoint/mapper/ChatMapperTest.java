package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ChatDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ClienteDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.MensagemConversaDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ChatMapperTest {

    private Chat chatDomain;

    @BeforeEach
    void setUp() {
        chatDomain = Chat.builder()
                .id(UUID.randomUUID())
                .dataCriacao(LocalDateTime.of(2025, Month.OCTOBER, 8, 15, 58))
                .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                .mensagensChat(List.of(MensagemConversa.builder().id(UUID.randomUUID()).build(), MensagemConversa.builder().id(UUID.randomUUID()).build()))
                .build();
    }

    @Test
    void deveRetornarDtoComSucesso() {
        ChatDto resultado = ChatMapper.paraDto(chatDomain);

        Assertions.assertEquals(chatDomain.getId(), resultado.getId());
        Assertions.assertEquals(chatDomain.getDataCriacao(), resultado.getDataCriacao());
        Assertions.assertEquals(chatDomain.getCliente().getId(), resultado.getCliente().getId());
        Assertions.assertEquals(chatDomain.getMensagensChat().get(0).getId(), resultado.getMensagensChat().get(0).getId());
        Assertions.assertEquals(chatDomain.getMensagensChat().get(1).getId(), resultado.getMensagensChat().get(1).getId());

    }
}