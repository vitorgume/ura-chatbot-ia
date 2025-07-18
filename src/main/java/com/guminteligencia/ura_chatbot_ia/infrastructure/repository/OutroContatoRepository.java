package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutroContatoRepository extends JpaRepository<OutroContatoEntity, Long> {
    Optional<OutroContatoEntity> findByNome(String nome);
}
