package com.guminteligencia.ura_chatbot_ia.infrastructure.mapper;

import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;

public class ConversaAgenteMapper {
    public static ConversaAgenteEntity paraEntity(ConversaAgente domain) {

        if(domain.getVendedor() == null) {
            return ConversaAgenteEntity.builder()
                    .id(domain.getId())
                    .cliente(ClienteMapper.paraEntity(domain.getCliente()))
                    .vendedor(null)
                    .dataCriacao(domain.getDataCriacao())
                    .finalizada(domain.getFinalizada())
                    .inativa(domain.getInativa())
                    .dataUltimaMensagem(domain.getDataUltimaMensagem())
                    .recontato(domain.getRecontato())
                    .inativo(domain.getInativo())
                    .build();

        } else {
            return ConversaAgenteEntity.builder()
                    .id(domain.getId())
                    .cliente(ClienteMapper.paraEntity(domain.getCliente()))
                    .vendedor(VendedorMapper.paraEntity(domain.getVendedor()))
                    .dataCriacao(domain.getDataCriacao())
                    .finalizada(domain.getFinalizada())
                    .inativa(domain.getInativa())
                    .dataUltimaMensagem(domain.getDataUltimaMensagem())
                    .recontato(domain.getRecontato())
                    .inativo(domain.getInativo())
                    .build();
        }
    }

    public static ConversaAgente paraDomain(ConversaAgenteEntity entity) {
        if(entity.getVendedor() == null) {
            return ConversaAgente.builder()
                    .id(entity.getId())
                    .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                    .vendedor(null)
                    .dataCriacao(entity.getDataCriacao())
                    .finalizada(entity.getFinalizada())
                    .inativa(entity.getInativa())
                    .dataUltimaMensagem(entity.getDataUltimaMensagem())
                    .recontato(entity.getRecontato())
                    .inativo(entity.getInativo())
                    .build();
        } else {
            return ConversaAgente.builder()
                    .id(entity.getId())
                    .cliente(ClienteMapper.paraDomain(entity.getCliente()))
                    .vendedor(VendedorMapper.paraDomain(entity.getVendedor()))
                    .dataCriacao(entity.getDataCriacao())
                    .finalizada(entity.getFinalizada())
                    .inativa(entity.getInativa())
                    .dataUltimaMensagem(entity.getDataUltimaMensagem())
                    .recontato(entity.getRecontato())
                    .inativo(entity.getInativo())
                    .build();
        }

    }
}
