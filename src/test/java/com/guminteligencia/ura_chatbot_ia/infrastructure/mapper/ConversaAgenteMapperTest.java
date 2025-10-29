package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConversaAgenteMapperTest {

    private ConversaAgente conversaAgenteDomain;
    private ConversaAgenteEntity conversaAgenteEntity;

    @BeforeEach
    void setUp() {
        conversaAgenteDomain = ConversaAgente.builder()
                .id(UUID.randomUUID())
                .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                .vendedor(Vendedor.builder().id(1L).build())
                .dataCriacao(LocalDateTime.now())
                .finalizada(false)
                .dataUltimaMensagem(LocalDateTime.now().plusHours(1))
                .recontato(false)
                .build();

        conversaAgenteEntity = ConversaAgenteEntity.builder()
                .id(UUID.randomUUID())
                .cliente(ClienteEntity.builder().id(UUID.randomUUID()).build())
                .vendedor(VendedorEntity.builder().id(1L).build())
                .dataCriacao(LocalDateTime.now().plusDays(1))
                .finalizada(true)
                .dataUltimaMensagem(LocalDateTime.now().plusDays(1).plusHours(1))
                .recontato(true)
                .build();

    }

    @Test
    void deveTrasformarParaEntityVendedorNaoNulo() {
        ConversaAgenteEntity conversaAgenteTeste = ConversaAgenteMapper.paraEntity(conversaAgenteDomain);

        Assertions.assertEquals(conversaAgenteTeste.getId(), conversaAgenteDomain.getId());
        Assertions.assertEquals(conversaAgenteTeste.getCliente().getId(), conversaAgenteDomain.getCliente().getId());
        Assertions.assertEquals(conversaAgenteTeste.getVendedor().getId(), conversaAgenteDomain.getVendedor().getId());
        Assertions.assertEquals(conversaAgenteTeste.getDataCriacao(), conversaAgenteDomain.getDataCriacao());
        Assertions.assertFalse(conversaAgenteTeste.getFinalizada());
        Assertions.assertEquals(conversaAgenteTeste.getDataUltimaMensagem(), conversaAgenteDomain.getDataUltimaMensagem());
        Assertions.assertFalse(conversaAgenteTeste.getRecontato());
    }

    @Test
    void deveTrasformarParaEntityVendedorNulo() {
        conversaAgenteDomain.setVendedor(null);

        ConversaAgenteEntity conversaAgenteTeste = ConversaAgenteMapper.paraEntity(conversaAgenteDomain);

        Assertions.assertEquals(conversaAgenteTeste.getId(), conversaAgenteDomain.getId());
        Assertions.assertEquals(conversaAgenteTeste.getCliente().getId(), conversaAgenteDomain.getCliente().getId());
        Assertions.assertNull(conversaAgenteTeste.getVendedor());
        Assertions.assertEquals(conversaAgenteTeste.getDataCriacao(), conversaAgenteDomain.getDataCriacao());
        Assertions.assertFalse(conversaAgenteTeste.getFinalizada());
        Assertions.assertEquals(conversaAgenteTeste.getDataUltimaMensagem(), conversaAgenteDomain.getDataUltimaMensagem());
        Assertions.assertFalse(conversaAgenteTeste.getRecontato());
    }

    @Test
    void deveTrasformarParaDomainVendedorNaoNulo() {
        ConversaAgente conversaAgenteTeste = ConversaAgenteMapper.paraDomain(conversaAgenteEntity);

        Assertions.assertEquals(conversaAgenteTeste.getId(), conversaAgenteEntity.getId());
        Assertions.assertEquals(conversaAgenteTeste.getCliente().getId(), conversaAgenteEntity.getCliente().getId());
        Assertions.assertEquals(conversaAgenteTeste.getVendedor().getId(), conversaAgenteEntity.getVendedor().getId());
        Assertions.assertEquals(conversaAgenteTeste.getDataCriacao(), conversaAgenteEntity.getDataCriacao());
        Assertions.assertTrue(conversaAgenteTeste.getFinalizada());
        Assertions.assertEquals(conversaAgenteTeste.getDataUltimaMensagem(), conversaAgenteEntity.getDataUltimaMensagem());
        Assertions.assertTrue(conversaAgenteTeste.getRecontato());
    }

    @Test
    void deveTrasformarParaDomainVendedorNulo() {
        conversaAgenteEntity.setVendedor(null);

        ConversaAgente conversaAgenteTeste = ConversaAgenteMapper.paraDomain(conversaAgenteEntity);

        Assertions.assertEquals(conversaAgenteTeste.getId(), conversaAgenteEntity.getId());
        Assertions.assertEquals(conversaAgenteTeste.getCliente().getId(), conversaAgenteEntity.getCliente().getId());
        Assertions.assertNull(conversaAgenteTeste.getVendedor());
        Assertions.assertEquals(conversaAgenteTeste.getDataCriacao(), conversaAgenteEntity.getDataCriacao());
        Assertions.assertTrue(conversaAgenteTeste.getFinalizada());
        Assertions.assertEquals(conversaAgenteTeste.getDataUltimaMensagem(), conversaAgenteEntity.getDataUltimaMensagem());
        Assertions.assertTrue(conversaAgenteTeste.getRecontato());
    }
}