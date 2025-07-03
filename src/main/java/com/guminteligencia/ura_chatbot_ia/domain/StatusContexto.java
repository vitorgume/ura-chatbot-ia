package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusContexto {
    OBSOLETO(0),
    ATIVO(1);

    private final int codigo;
}
