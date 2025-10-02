package com.guminteligencia.ura_chatbot_ia.entrypoint.dto;

import com.guminteligencia.ura_chatbot_ia.domain.Canal;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ClienteDto {
    private UUID id;
    private String nome;
    private String telefone;
    private Regiao regiao;
    private Segmento segmento;
    private boolean inativo;
    private String descricaoMaterial;
    private Canal canal;
}
