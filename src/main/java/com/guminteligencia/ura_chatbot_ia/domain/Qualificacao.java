package com.guminteligencia.ura_chatbot_ia.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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

    @JsonProperty("descricao_material")
    private String descricaoMaterial;

    @JsonProperty("endereco_real")
    private String enderecoReal;
}
