package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class RelatorioOnlineDto {
    private String data;
    private String cliente;
    private String telefone;
    private String vendedor;
}
