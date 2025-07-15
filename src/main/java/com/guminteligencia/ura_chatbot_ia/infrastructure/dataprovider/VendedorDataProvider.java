package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.VendedorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.VendedorMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.VendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class VendedorDataProvider implements VendedorGateway {

    private final String MENSAGEM_ERRO_LISTAR_COM_EXCECAO = "Erro ao listar todos os vendedores com excecao.";
    private final String MENSAGEM_LISTAR_VENDEDORES = "Erro ao listar vendedores.";
    private final String MENSAGEM_ERRO_CONSULTAR_VENDEDOR_PELO_NOME = "Erro ao consultar vendedor pelo seu nome.";
    private final VendedorRepository repository;

    @Override
    public Optional<Vendedor> consultarVendedor(String nome) {
        Optional<VendedorEntity> vendedorEntity;

        try {
            vendedorEntity = repository.findByNome(nome);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_VENDEDOR_PELO_NOME, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_VENDEDOR_PELO_NOME, ex.getCause());
        }

        return vendedorEntity.map(VendedorMapper::paraDomain);
    }

    @Override
    public List<Vendedor> listar() {
        List<VendedorEntity> vendedorEntities;

        try {
            vendedorEntities = repository.findAll();
        } catch (Exception ex) {
            log.error(MENSAGEM_LISTAR_VENDEDORES, ex);
            throw new DataProviderException(MENSAGEM_LISTAR_VENDEDORES, ex.getCause());
        }

        return vendedorEntities.stream().map(VendedorMapper::paraDomain).toList();
    }

    @Override
    public List<Vendedor> listarComExcecao(String excecao) {
        List<VendedorEntity> vendedorEntities;

        try {
            vendedorEntities = repository.listarComExcecao(excecao);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_COM_EXCECAO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_COM_EXCECAO, ex.getCause());
        }

        return vendedorEntities.stream().map(VendedorMapper::paraDomain).toList();
    }
}
