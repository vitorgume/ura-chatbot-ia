package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Chat {
    private UUID id;
    private LocalDateTime dataCriacao;
    private Cliente cliente;
    private List<MensagemConversa> mensagensChat;
}
