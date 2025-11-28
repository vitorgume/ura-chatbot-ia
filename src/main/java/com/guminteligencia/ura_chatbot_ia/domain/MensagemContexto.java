package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MensagemContexto {
    private String mensagem;
    private String imagemUrl;
    private String audioUrl;
}
