package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Segmento {
    MEDICINA_SAUDE(1, "Medicina e Saúde", 1242451),
    BOUTIQUE_LOJAS(2, "Boutique e Lojas", 1242453),
    ENGENHARIA_ARQUITETURA(3, "Engenharia e Arquitetura", 1242455),
    ALIMENTOS(4, "Alimentos", 1242457),
    CELULARES(5, "Celulares", 1243159),
    OUTROS(6, "Outros", 1242459),
    NAO_INFORMADO(7, "Não informado",1242461);

    private final int codigo;
    private final String descricao;
    private final int idCrm;
}
