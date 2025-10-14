package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemConversaRepository extends JpaRepository<MensagemConversaEntity, UUID> {

    @EntityGraph(attributePaths = {
            "conversaAgente",
            "conversaAgente.vendedor",
            "conversaAgente.cliente"// <- precisa desse
            // adicione outros que o mapper tocar: "conversaAgente.cliente", etc.
    })
    List<MensagemConversaEntity> findByConversaAgente_Id(UUID conversaId);
}
