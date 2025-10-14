package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MidiaCliente {
    private UUID id;
    private String telefoneCliente;
    private List<String> urlMidias;
}
