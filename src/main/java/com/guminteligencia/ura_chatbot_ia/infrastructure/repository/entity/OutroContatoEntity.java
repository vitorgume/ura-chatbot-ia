package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "OutroContato")
@Table(name = "outros_contatos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OutroContatoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_outro_contato")
    private Long id;
    private String nome;
    private String telefone;
    private String descricao;
}
