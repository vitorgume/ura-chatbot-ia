package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.UUID;

class ChatMapperTest {

    private Chat chatDomain;
    private ChatEntity chatEntity;

    @BeforeEach
    void setUp() {
        chatDomain = Chat.builder()
                .id(UUID.randomUUID())
                .dataCriacao(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 53))
                .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                .mensagensChat(List.of(
                        MensagemConversa.builder()
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
                                .build(),
                        MensagemConversa.builder()
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
                                .build()
                ))
                .build();

        chatEntity = ChatEntity.builder()
                .id(UUID.randomUUID())
                .dataCriacao(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 53))
                .cliente(ClienteEntity.builder().id(UUID.randomUUID()).build())
                .mensagensChat(List.of(
                        MensagemConversaEntity.builder()
                                .id(UUID.randomUUID())
                                .responsavel("usuario")
                                .conteudo("teste 123")
                                .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                                .conversaAgente(ConversaAgenteEntity.builder()
                                        .id(UUID.randomUUID())
                                        .cliente(ClienteEntity.builder().id(UUID.randomUUID()).build())
                                        .vendedor(VendedorEntity.builder().id(1L).build())
                                        .build()
                                )
                                .build(),
                        MensagemConversaEntity.builder()
                                .id(UUID.randomUUID())
                                .responsavel("usuario")
                                .conteudo("teste 123")
                                .data(LocalDateTime.of(2025, Month.OCTOBER, 8, 14, 41))
                                .conversaAgente(ConversaAgenteEntity.builder()
                                        .id(UUID.randomUUID())
                                        .cliente(ClienteEntity.builder().id(UUID.randomUUID()).build())
                                        .vendedor(VendedorEntity.builder().id(1L).build())
                                        .build()
                                )
                                .build()
                ))
                .build();
    }

    @Test
    void deveRetornarDomainComSucesso() {
        Chat resultado = ChatMapper.paraDomain(chatEntity);

        Assertions.assertEquals(chatEntity.getId(), resultado.getId());
        Assertions.assertEquals(chatEntity.getDataCriacao(), resultado.getDataCriacao());
        Assertions.assertEquals(chatEntity.getCliente().getId(), resultado.getCliente().getId());
        Assertions.assertEquals(chatEntity.getMensagensChat().get(0).getId(), resultado.getMensagensChat().get(0).getId());
        Assertions.assertEquals(chatEntity.getMensagensChat().get(1).getId(), resultado.getMensagensChat().get(1).getId());
    }

    @Test
    void deveRetornarEntityComSucesso() {
        ChatEntity resultado = ChatMapper.paraEntity(chatDomain);

        Assertions.assertEquals(chatDomain.getId(), resultado.getId());
        Assertions.assertEquals(chatDomain.getDataCriacao(), resultado.getDataCriacao());
        Assertions.assertEquals(chatDomain.getCliente().getId(), resultado.getCliente().getId());
        Assertions.assertEquals(chatDomain.getMensagensChat().get(0).getId(), resultado.getMensagensChat().get(0).getId());
        Assertions.assertEquals(chatDomain.getMensagensChat().get(1).getId(), resultado.getMensagensChat().get(1).getId());
    }
}