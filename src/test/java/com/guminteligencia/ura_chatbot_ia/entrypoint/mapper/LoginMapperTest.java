package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

@Slf4j
class LoginMapperTest {

    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        loginResponse = LoginResponse.builder()
                .token("Token teste")
                .id(UUID.randomUUID())
                .build();
    }

    @Test
    void deveRetornarDto() {
        LoginResponseDto loginResponseDto = LoginMapper.paraDto(loginResponse);

        Assertions.assertEquals(loginResponse.getToken(), loginResponseDto.getToken());
        Assertions.assertEquals(loginResponse.getId(), loginResponseDto.getId());
    }
}