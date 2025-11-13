package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class ResponseDtoTest {

    private ResponseDto.ErroDto erroDto;

    @BeforeEach
    void setUp() {
        erroDto = new ResponseDto.ErroDto(List.of("Msg1", "Msg2"));
    }

    @Test
    void deveRetornarResponseDtoComErro() {
        ResponseDto<Object> responseTeste = ResponseDto.comErro(erroDto);

        Assertions.assertNotNull(responseTeste.getErro());
        Assertions.assertEquals(responseTeste.getErro(), erroDto);
    }
}