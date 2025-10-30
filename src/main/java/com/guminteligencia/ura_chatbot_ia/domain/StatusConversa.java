package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusConversa {
    INATIVO_G1(0, "Inativo grau 1"),
    INATIVO_G2(1, "Inativo grau 2"),
    ATIVO(2, "Conversa Ativa"),
    ANDAMENTO(3, "Conversa em andamento");

    private final Integer codigo;
    private final String descricao;
}
