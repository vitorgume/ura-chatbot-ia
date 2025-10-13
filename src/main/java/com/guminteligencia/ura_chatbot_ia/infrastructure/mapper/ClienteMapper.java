package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;

import java.util.function.Function;

public class ClienteMapper {
    public static Cliente paraDomain(ClienteEntity entity) {
        return Cliente.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .telefone(entity.getTelefone())
                .regiao(entity.getRegiao())
                .segmento(entity.getSegmento())
                .inativo(entity.isInativo())
                .descricaoMaterial(entity.getDescricaoMaterial())
                .canal(entity.getCanal())
                .regiaoReal(entity.getRegiaoReal())
                .build();
    }

    public static ClienteEntity paraEntity(Cliente domain) {
        return ClienteEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .telefone(domain.getTelefone())
                .regiao(domain.getRegiao())
                .segmento(domain.getSegmento())
                .inativo(domain.isInativo())
                .descricaoMaterial(domain.getDescricaoMaterial())
                .canal(domain.getCanal())
                .regiaoReal(domain.getRegiaoReal())
                .build();
    }
}
