package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Qualificacao {
    private Boolean qualificado;
    private String nome;
    private int segmento;
    private int regiao;
}
