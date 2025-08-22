package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Cliente {
    private UUID id;
    private String nome;
    private String telefone;
    private Regiao regiao;
    private Segmento segmento;
    private boolean inativo;
    private String descricaoMaterial;
    private Canal canal;

    public void setDados(Cliente cliente) {
        this.nome = cliente.getNome();
        this.regiao = cliente.getRegiao();
        this.segmento = cliente.getSegmento();
        this.descricaoMaterial = cliente.getDescricaoMaterial();
    }
}
