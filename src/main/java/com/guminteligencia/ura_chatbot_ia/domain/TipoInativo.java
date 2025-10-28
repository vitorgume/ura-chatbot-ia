package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TipoInativo {
    INATIVO_G1(0, "Inativo grau 1"),
    INATIVO_G2(1, "Inativo grau 2");

    private final Integer codigo;
    private final String descricao;
}
