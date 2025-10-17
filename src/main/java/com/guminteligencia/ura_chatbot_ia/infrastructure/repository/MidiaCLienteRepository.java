package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaCLienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MidiaCLienteRepository extends JpaRepository<MidiaCLienteEntity, UUID> {

    @Query("select m from MidiaCliente m left join fetch m.urlMidias where m.telefoneCliente = :tel")
    Optional<MidiaCLienteEntity> findByTelefoneClienteFetch(@Param("tel") String telefone);

    void deleteByTelefoneCliente(String telefone);
}
