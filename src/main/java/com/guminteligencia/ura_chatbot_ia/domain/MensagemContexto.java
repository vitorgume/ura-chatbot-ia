package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class MensagemContexto {
    private String mensagem;
    private String imagemUrl;
    private String audioUrl;
}
