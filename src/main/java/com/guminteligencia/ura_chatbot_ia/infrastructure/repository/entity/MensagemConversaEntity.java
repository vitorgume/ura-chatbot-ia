package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity(name = "MensagemConversa")
@Table(name = "mensagens_conversa")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MensagemConversaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_mensagem_conversa")
    private UUID id;
    private String responsavel;
    private String conteudo;
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "id_conversa",
            referencedColumnName = "id_conversa",
            foreignKey = @ForeignKey(name = "fk_msg_conversa")
    )
    private ConversaAgenteEntity conversaAgente;
}
