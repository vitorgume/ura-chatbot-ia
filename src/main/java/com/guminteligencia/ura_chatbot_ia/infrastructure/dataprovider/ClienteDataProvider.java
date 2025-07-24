package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ClienteGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ClienteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.RelatorioMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ClienteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ClienteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ClienteDataProvider implements ClienteGateway {

    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar cliente pelo seu id.";
    private final String MENSAGEM_ERRO_SALVAR_CLIENTE = "Erro ao salvar cliente.";
    private final String MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE = "Erro ao consultar cliente pelo seu telefone.";
    private final String MENSAGEM_ERRO_GERAR_RELATORIO = "Erro ao gerar relatório de contatos.";
    private final String MENSAGEM_ERRO_GERAR_RELATORIO_SEGUNDA_FEIRA = "Erro ao gerar relatório de segunda feira.";
    private final ClienteRepository repository;

    @Override
    public Optional<Cliente> consultarPorTelefone(String telefone) {
        Optional<ClienteEntity> clienteEntity;

        try {
            clienteEntity = repository.findByTelefone(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_TELEFONE, ex.getCause());
        }

        return clienteEntity.map(ClienteMapper::paraDomain);
    }

    @Override
    public Cliente salvar(Cliente cliente) {
        ClienteEntity clienteEntity = ClienteMapper.paraEntity(cliente);

        try {
            clienteEntity = repository.save(clienteEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_CLIENTE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_CLIENTE, ex.getCause());
        }

        return ClienteMapper.paraDomain(clienteEntity);
    }

    @Override
    public Optional<Cliente> consultarPorId(UUID id) {
        Optional<ClienteEntity> clienteEntity;

        try {
            clienteEntity = repository.findById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }


        return clienteEntity.map(ClienteMapper::paraDomain);
    }

    @Override
    public List<RelatorioContatoDto> getRelatorioContato() {
        List<RelatorioContatoDto> relatorios;

        try {
            relatorios = RelatorioMapper.paraDto(repository.gerarRelatorio());
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_GERAR_RELATORIO, ex);
            throw new DataProviderException(MENSAGEM_ERRO_GERAR_RELATORIO, ex.getCause());
        }

        return relatorios;
    }

    @Override
    public List<RelatorioContatoDto> getRelatorioContatoSegundaFeira() {
        List<RelatorioContatoDto> relatorios;

        try {
            relatorios = RelatorioMapper.paraDto(repository.gerarRelatorioSegundaFeira());
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_GERAR_RELATORIO_SEGUNDA_FEIRA, ex);
            throw new DataProviderException(MENSAGEM_ERRO_GERAR_RELATORIO_SEGUNDA_FEIRA, ex.getCause());
        }

        return relatorios;
    }
}
