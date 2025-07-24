package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Builder
@Getter
@Setter
public class DocumentoRequestDto {
    private String phone;
    private String document;
    private String fileName;
}
