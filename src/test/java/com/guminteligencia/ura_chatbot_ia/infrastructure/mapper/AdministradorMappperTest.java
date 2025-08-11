package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.AdministradorEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class AdministradorMappperTest {

    private Administrador administradorDomain;
    private AdministradorEntity administradorEntity;

    @BeforeEach
    void setUp() {
        administradorDomain = Administrador.builder()
                .id(UUID.randomUUID())
                .nome("Nome domain")
                .senha("senhadomain123")
                .telefone("emaildomain@gmail.com")
                .build();

        administradorEntity = AdministradorEntity.builder()
                .id(UUID.randomUUID())
                .nome("Nome emtity")
                .senha("senhaentity123")
                .telefone("emailentity@gmail.com")
                .build();
    }

    @Test
    void deveTrasformarParaDomain() {
        Administrador administradorTeste = AdministradorMappper.paraDomain(administradorEntity);

        Assertions.assertEquals(administradorTeste.getId(), administradorEntity.getId());
        Assertions.assertEquals(administradorTeste.getNome(), administradorEntity.getNome());
        Assertions.assertEquals(administradorTeste.getSenha(), administradorEntity.getSenha());
        Assertions.assertEquals(administradorTeste.getTelefone(), administradorEntity.getTelefone());
    }

    @Test
    void deveTrasformarParaEntity() {
        AdministradorEntity administradorTeste = AdministradorMappper.paraEntity(administradorDomain);

        Assertions.assertEquals(administradorTeste.getId(), administradorDomain.getId());
        Assertions.assertEquals(administradorTeste.getNome(), administradorDomain.getNome());
        Assertions.assertEquals(administradorTeste.getSenha(), administradorDomain.getSenha());
        Assertions.assertEquals(administradorTeste.getTelefone(), administradorDomain.getTelefone());
    }
}