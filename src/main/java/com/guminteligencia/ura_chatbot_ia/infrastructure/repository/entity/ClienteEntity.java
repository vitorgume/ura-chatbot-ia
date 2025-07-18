package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "Cliente")
@Table(name = "clientes")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ClienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_cliente")
    private UUID id;
    private String nome;
    private String telefone;

    @Enumerated(EnumType.ORDINAL)
    private Regiao regiao;

    @Enumerated(EnumType.ORDINAL)
    private Segmento segmento;

    private String descricaoMaterial;

    private boolean inativo;
}
