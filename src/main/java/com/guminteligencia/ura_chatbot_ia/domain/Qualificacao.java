package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class Qualificacao {
    private Boolean qualificado;
    private String nome;
    private int segmento;
    private int regiao;
}
