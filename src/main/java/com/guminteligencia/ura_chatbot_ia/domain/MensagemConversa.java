package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MensagemConversa {
    private UUID id;
    private String responsavel;
    private String conteudo;
    private LocalDateTime data;
    private ConversaAgente conversaAgente;
}
