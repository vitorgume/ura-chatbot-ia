package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private UUID id;
}
