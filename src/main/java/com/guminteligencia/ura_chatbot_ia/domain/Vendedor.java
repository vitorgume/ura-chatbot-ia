package com.guminteligencia.ura_chatbot_ia.domain;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Vendedor {
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private List<Segmento> segmentos;
    private List<Regiao> regioes;
    private Prioridade prioridade;

    public void setDados(Vendedor novosDados) {
        this.nome = novosDados.getNome();
        this.telefone = novosDados.getTelefone();
        this.inativo = novosDados.getInativo();
        this.segmentos = novosDados.getSegmentos();
        this.regioes = novosDados.getRegioes();
        this.prioridade = novosDados.getPrioridade();
    }
}
