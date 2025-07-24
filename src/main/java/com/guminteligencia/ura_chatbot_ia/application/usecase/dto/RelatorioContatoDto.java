package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class RelatorioContatoDto {
    private String nome;
    private String telefone;
    private Segmento segmento;
    private Regiao regiao;
    private LocalDateTime dataCriacao;
    private String nomeVendedor;
}
