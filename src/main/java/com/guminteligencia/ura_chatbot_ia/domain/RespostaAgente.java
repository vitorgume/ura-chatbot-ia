package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RespostaAgente {
    private String resposta;
    private Qualificacao qualificacao;
}
