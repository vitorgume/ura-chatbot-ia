package com.guminteligencia.ura_chatbot_ia.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
@Embeddable
public class Prioridade {
    private Integer valor;
    private Boolean prioritario;
}
