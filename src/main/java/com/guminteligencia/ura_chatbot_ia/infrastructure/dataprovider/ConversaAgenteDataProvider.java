package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ConversaAgenteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ConversaAgenteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ConversaAgenteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ConversaAgenteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ConversaAgenteDataProvider implements ConversaAgenteGateway {

    private final String MENSAGEM_ERRO_LISTAR_CONVERSAS_NAO_FINALIZADAS = "Erro ao listar conversas não finalizadas.";
    private final String MENSAGEM_ERRO_CONSULTAR_PELO_ID = "Erro ao consultar conversa pelo seu id.";
    private final String MENSAGEM_ERRO_CONSULTAR_ID_CLIENTE = "Erro ao consultar conversa pelo id do cliente.";
    private final String MENSAGEM_ERRO_SALVAR_CONVERSA_AGENTE = "Erro ao salvar conversa do agente.";
    private final ConversaAgenteRepository repository;

    @Override
    public ConversaAgente salvar(ConversaAgente conversaAgente) {
        ConversaAgenteEntity conversaAgenteEntity = ConversaAgenteMapper.paraEntity(conversaAgente);

        try {
            conversaAgenteEntity = repository.save(conversaAgenteEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR_CONVERSA_AGENTE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR_CONVERSA_AGENTE, ex.getCause());
        }

        return ConversaAgenteMapper.paraDomain(conversaAgenteEntity);
    }

    @Override
    public Optional<ConversaAgente> consultarPorIdCliente(UUID id) {
        Optional<ConversaAgenteEntity> conversaAgenteEntity;

        try {
            conversaAgenteEntity = repository.findByCliente_Id(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_ID_CLIENTE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_ID_CLIENTE, ex.getCause());
        }

        return conversaAgenteEntity.map(ConversaAgenteMapper::paraDomain);
    }

    @Override
    public Optional<ConversaAgente> consultarPorId(UUID idConversa) {
        Optional<ConversaAgenteEntity> conversaAgenteEntity;

        try {
            conversaAgenteEntity = repository.findById(idConversa);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_PELO_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_PELO_ID, ex.getCause());
        }

        return conversaAgenteEntity.map(ConversaAgenteMapper::paraDomain);
    }

    @Override
    public List<ConversaAgente> listarNaoFinalizados() {
        List<ConversaAgenteEntity> conversasEntity;

        try {
            conversasEntity = repository.listarNaoFinalizadas();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_CONVERSAS_NAO_FINALIZADAS, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_CONVERSAS_NAO_FINALIZADAS, ex.getCause());
        }

        return conversasEntity.stream().map(ConversaAgenteMapper::paraDomain).toList();
    }
}
