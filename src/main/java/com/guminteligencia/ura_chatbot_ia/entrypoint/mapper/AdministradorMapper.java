package com.guminteligencia.ura_chatbot_ia.entrypoint.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.AdministradorDto;

public class AdministradorMapper {

    public static Administrador paraDomain(AdministradorDto dto) {
        return Administrador.builder()
                .id(dto.getId())
                .nome(dto.getNome())
                .senha(dto.getSenha())
                .telefone(dto.getTelefone())
                .build();
    }

    public static AdministradorDto paraDto(Administrador domain) {
        return AdministradorDto.builder()
                .id(domain.getId())
                .nome(domain.getNome())
                .senha(domain.getSenha())
                .telefone(domain.getTelefone())
                .build();
    }
}
