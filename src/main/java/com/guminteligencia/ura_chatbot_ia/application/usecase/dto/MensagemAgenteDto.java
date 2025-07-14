package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MensagemAgenteDto {
    private String clienteId;
    private String conversaId;
    private String mensagem;
}
