package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Contexto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ContextoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ContextoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ContextoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContextoDataProvider implements ContextoGateway {

    private final String MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_ID = "Erro ao consultar contexto pelo seu id.";
    private final String MENSAGEM_ERRO_DELETAR_CONTEXTO_BD = "Erro ao deletar contexto do banco de dados.";
    private final ContextoRepository repository;

    @Override
    public void deletar(UUID id) {
        try {
            repository.deletar(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_CONTEXTO_BD, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_CONTEXTO_BD, ex.getCause());
        }
    }

    @Override
    public Optional<Contexto> consultarPorId(UUID id) {
        Optional<ContextoEntity> contextoEntity;

        try {
            contextoEntity = repository.consultarPorId(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_CONTEXTO_PELO_ID, ex.getCause());
        }

        return contextoEntity.map(ContextoMapper::paraDomain);
    }
}
