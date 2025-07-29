package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginResponseDto;

import java.util.UUID;

public class LoginMapper {

    public static LoginResponseDto paraDto(LoginResponse loginResponse) {
        return new LoginResponseDto(loginResponse.getToken(), loginResponse.getId());
    }
}
