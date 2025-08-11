package com.guminteligencia.ura_chatbot_ia.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
@ToString
@NoArgsConstructor
public class Prioridade {
    private Integer valor;
    private Boolean prioritario;
}
