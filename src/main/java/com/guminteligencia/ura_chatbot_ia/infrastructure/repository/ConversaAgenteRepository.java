package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversaAgenteRepository extends JpaRepository<ConversaAgenteEntity, UUID> {
    Optional<ConversaAgenteEntity> findByCliente_Id(UUID id);

    @Query("SELECT c FROM ConversaAgente c WHERE c.finalizada = false")
    List<ConversaAgenteEntity> listarNaoFinalizadas();
}
