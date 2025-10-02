package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "Chat")
@Table(name = "chats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @OneToOne
    private ClienteEntity cliente;

    @OneToMany
    @JoinTable(
            name = "chats_mensagens_chat",
            joinColumns = @JoinColumn(name = "chat_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "mensagens_chat_id",
                    referencedColumnName = "id_mensagem_conversa"
            )
    )
    private List<MensagemConversaEntity> mensagensChat;
}
