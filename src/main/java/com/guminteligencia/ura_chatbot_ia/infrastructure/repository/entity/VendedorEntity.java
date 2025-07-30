package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity(name = "Vendedor")
@Table(name = "vendedores")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class VendedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vendedor")
    private Long id;
    private String nome;
    private String telefone;
    private Boolean inativo;
    private List<Segmento> segmentos;
    private List<Regiao> regioes;

    @Embedded
    private Prioridade prioridade;
}
