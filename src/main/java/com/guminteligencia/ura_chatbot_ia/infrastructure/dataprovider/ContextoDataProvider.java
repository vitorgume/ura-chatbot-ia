package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ContextoGateway;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ContextoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContextoDataProvider implements ContextoGateway {

    private final String MENSAGEM_ERRO_DELETAR_CONTEXTO_BD = "Erro ao deletar contexto do banco de dados.";
    private final ContextoRepository contextoRepository;

    @Override
    public void deletar(UUID id) {
        try {
            contextoRepository.deletar(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR_CONTEXTO_BD, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR_CONTEXTO_BD, ex.getCause());
        }
    }
}
