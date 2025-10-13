package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Regiao {
    MARINGA(1, "Maringá", 1242463),
    REGIAO_MARINGA(2, "Região de Maringá", 1242465),
    OUTRA(3, "Outras regiões", 1242467),
    NAO_INFORMADA(4, "Não informada", 1242469);

    private final int codigo;
    private final String descricao;
    private int idCrm;
}
