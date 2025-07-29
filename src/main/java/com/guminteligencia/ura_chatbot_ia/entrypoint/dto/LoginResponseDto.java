package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class LoginResponseDto {
    private String token;
    private UUID id;
}
