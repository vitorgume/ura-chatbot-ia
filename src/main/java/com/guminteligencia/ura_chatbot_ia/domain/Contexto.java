package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@NoArgsConstructor
@ToString
public class Contexto {
    private UUID id;
    private String telefone;
    private List<String> mensagens;
    private StatusContexto status;
    private Message mensagemFila;
}
