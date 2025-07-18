package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.OutroContatoGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.OutroContatoMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.OutroContatoRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.OutroContatoEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class OutroContatoDataProvider implements OutroContatoGateway {

    private final String MENSAGEM_ERRO_CONSULTAR_POR_NOME = "Erro ao consultar por nome outro contato.";
    private final OutroContatoRepository repository;

    @Override
    public Optional<OutroContato> consultarPorNome(String nome) {
        Optional<OutroContatoEntity> outroContatoEntity;

        try {
            outroContatoEntity = repository.findByNome(nome);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_NOME, ex.getCause());
        }

        return outroContatoEntity.map(OutroContatoMapper::paraDomain);
    }
}
