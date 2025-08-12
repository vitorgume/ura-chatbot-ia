package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Canal {
    CHATBOT(0),
    URA(1);

    private final int codigo;
}
