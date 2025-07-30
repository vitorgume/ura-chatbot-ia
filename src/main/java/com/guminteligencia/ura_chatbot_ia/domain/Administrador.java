package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
public class Administrador {
    private UUID id;
    private String nome;
    private String senha;
    private String email;
}
