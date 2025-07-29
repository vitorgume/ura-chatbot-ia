package com.guminteligencia.ura_chatbot_ia.infrastructure.repository;

import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendedorRepository extends JpaRepository<VendedorEntity, Long> {
    Optional<VendedorEntity> findByNome(String nome);

    @Query("SELECT v FROM Vendedor v WHERE v.nome <> :excecao")
    List<VendedorEntity> listarComExcecao(String excecao);

    Optional<VendedorEntity> findByTelefone(String telefone);

    List<VendedorEntity> findByInativoIsFalse();
}
