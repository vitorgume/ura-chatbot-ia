package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ChatDto {

    private UUID id;
    private LocalDateTime dataCriacao;
    private ClienteDto cliente;
    private List<MensagemConversaDto> mensagensChat;

}
