package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VendedorDto {
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private List<Segmento> segmentos;
    private List<Regiao> regioes;
}
