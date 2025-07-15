package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;

public class ConversaAgenteMapper {
    public static ConversaAgenteEntity paraEntity(ConversaAgente domain) {
        return ConversaAgenteEntity.builder()
                .id(domain.getId())
                .cliente(ClienteMapper.paraEntity(domain.getCliente()))
                .vendedor(VendedorMapper.paraEntity(domain.getVendedor()))
                .dataCriacao(domain.getDataCriacao())
                .finalizada(domain.getFinalizada())
                .inativa(domain.getInativa())
                .build();
    }

    public static ConversaAgente paraDomain(ConversaAgenteEntity entity) {
        return ConversaAgente.builder()
                .id(entity.getId())
                .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                .vendedor(VendedorMapper.paraDomain(entity.getVendedor()))
                .dataCriacao(entity.getDataCriacao())
                .finalizada(entity.getFinalizada())
                .inativa(entity.getInativa())
                .build();
    }
}
