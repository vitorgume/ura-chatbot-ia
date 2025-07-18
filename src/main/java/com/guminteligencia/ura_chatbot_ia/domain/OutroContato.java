package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OutroContato {
    private Long id;
    private String nome;
    private String telefone;
    private String descricao;
}
