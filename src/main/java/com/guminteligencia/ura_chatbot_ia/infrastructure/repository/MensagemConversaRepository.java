package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MensagemConversaRepository extends JpaRepository<MensagemConversaEntity, UUID> {
    List<MensagemConversaEntity> findByConversaAgente_Id(UUID conversaId);
}
