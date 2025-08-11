package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.AdministradorEntity;

public class AdministradorMappper {

    public static Administrador paraDomain(AdministradorEntity entity) {
        return Administrador.builder()
                .id(entity.getId())
                .nome(entity.getNome())
                .senha(entity.getSenha())
                .telefone(entity.getTelefone())
                .build();
    }

    public static AdministradorEntity paraEntity(Administrador domain) {
        return AdministradorEntity.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .senha(domain.getSenha())
                .telefone(domain.getTelefone())
                .build();
    }
}
