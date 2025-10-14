package com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity(name = "MidiaCliente")
@Table(name = "medias_clientes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MidiaCLienteEntity {

    @Id
    private UUID id;
    private String telefoneCliente;

    @ElementCollection
    @CollectionTable(
            name = "medias_clientes_urls",
            joinColumns = @JoinColumn(name = "midia_cliente_id")
    )
    @Column(name = "url", nullable = false, length = 2048)
    private List<String> urlMidias;
}
