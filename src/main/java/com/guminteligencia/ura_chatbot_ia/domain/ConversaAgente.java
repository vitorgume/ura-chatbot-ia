package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConversaAgente {
        private UUID id;
        private Cliente cliente;
        private Vendedor vendedor;
        private LocalDateTime dataCriacao;
}
