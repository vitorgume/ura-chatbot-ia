package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "MensagemChat")
@Table(name = "mensagens_chat")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MensagemConversaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String responsavel;
    private String conteudo;
    private LocalDateTime data;

    @ManyToOne
    private ConversaAgenteEntity conversaAgente;
}
