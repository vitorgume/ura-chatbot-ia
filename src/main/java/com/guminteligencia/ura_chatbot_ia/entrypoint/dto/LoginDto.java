package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class LoginDto {
    private String email;
    private String senha;
}
