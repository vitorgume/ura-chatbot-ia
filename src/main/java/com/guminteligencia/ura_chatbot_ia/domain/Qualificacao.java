package com.guminteligencia.ura_chatbot_ia.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.io.IOException;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class Qualificacao {
    private String nome;
    private int segmento;
    private int regiao;
    private String descricao_material;

    @JsonCreator
    public static Qualificacao fromJsonString(String json) {
        try {
            return new ObjectMapper()
                    // caso você queira ignorar campos extras:
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .readValue(json, Qualificacao.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Não foi possível desserializar Qualificacao a partir da string JSON", e);
        }
    }
}
