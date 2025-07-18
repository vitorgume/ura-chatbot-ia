package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "ConversaAgente")
@Table(name = "conversas_agente")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ConversaAgenteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_conversa")
    private UUID id;

    @OneToOne
    private ClienteEntity cliente;

    @ManyToOne
    private VendedorEntity vendedor;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_ultima_mensagem")
    private LocalDateTime dataUltimaMensagem;

    private Boolean finalizada;
    private Boolean inativa;
    private Boolean recontato;
}
