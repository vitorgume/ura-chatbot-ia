package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.AdministradorDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

class AdministradorMapperTest {

    private Administrador administradorDomain;
    private AdministradorDto administradorDto;

    @BeforeEach
    void setUp() {
        administradorDomain = Administrador.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste")
                .senha("Senha teste")
                .telefone("Email teste")
                .build();

        administradorDto = AdministradorDto.builder()
                .id(UUID.randomUUID())
                .nome("Nome teste 2")
                .senha("Senha teste 2")
                .telefone("Email teste 2")
                .build();
    }

    @Test
    void deveRetornarDomain() {
        Administrador administradorTeste = AdministradorMapper.paraDomain(administradorDto);

        Assertions.assertEquals(administradorDto.getId(), administradorTeste.getId());
        Assertions.assertEquals(administradorDto.getNome(), administradorTeste.getNome());
        Assertions.assertEquals(administradorDto.getSenha(), administradorTeste.getSenha());
        Assertions.assertEquals(administradorDto.getTelefone(), administradorTeste.getTelefone());
    }

    @Test
    void deveRetornarDto() {
        AdministradorDto administradorTeste = AdministradorMapper.paraDto(administradorDomain);

        Assertions.assertEquals(administradorDomain.getId(), administradorTeste.getId());
        Assertions.assertEquals(administradorDomain.getNome(), administradorTeste.getNome());
        Assertions.assertEquals(administradorDomain.getSenha(), administradorTeste.getSenha());
        Assertions.assertEquals(administradorDomain.getTelefone(), administradorTeste.getTelefone());
    }
}