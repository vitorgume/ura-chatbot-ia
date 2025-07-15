package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;

public class VendedorMapper {
    public static Vendedor paraDomain(VendedorEntity entity) {
        return Vendedor.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .inativo(entity.getInativo())
                .telefone(entity.getTelefone())
                .build();
    }

    public static VendedorEntity paraEntity(Vendedor domain) {
        return VendedorEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .inativo(domain.getInativo())
                .telefone(domain.getTelefone())
                .build();
    }
}
