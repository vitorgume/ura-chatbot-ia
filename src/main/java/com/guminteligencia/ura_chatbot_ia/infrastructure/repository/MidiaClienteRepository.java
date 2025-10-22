package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MidiaClienteRepository extends JpaRepository<MidiaClienteEntity, UUID> {

    @Query("select m from MidiaCliente m left join fetch m.urlMidias where m.telefoneCliente = :tel")
    Optional<MidiaClienteEntity> findByTelefoneClienteFetch(@Param("tel") String telefone);

    void deleteByTelefoneCliente(String telefone);
}
