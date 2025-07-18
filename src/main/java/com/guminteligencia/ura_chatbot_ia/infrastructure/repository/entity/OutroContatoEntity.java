package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "OutroContato")
@Table(name = "outros_contatos")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class OutroContatoEntity {
    private Long id;
    private String nome;
    private String telefone;
    private String descricao;
}
