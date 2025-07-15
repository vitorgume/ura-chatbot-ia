package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MensagemRequestWhatsAppDto {
    private String phone;
    private String message;
}
