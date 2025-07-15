package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.*;

@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class ContatoRequestDto {
    private String phone;
    private String contactName;
    private String contactPhone;
}
