package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemConversaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MensagemConversaMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MensagemConversaRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MensagemConversaEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class MensagemConversaDataProvider implements MensagemConversaGateway {

    private final MensagemConversaRepository repository;

    private final String MENSAGEM_ERRO_LISTAR_POR_CONVERSA = "Erro ao listar mensagens da convesa.";

    @Override
    public List<MensagemConversa> listarPelaConversa(UUID idConversa) {
        List<MensagemConversaEntity> mensagens;

        try {
            mensagens = repository.findByConversaAgente_Id(idConversa);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_LISTAR_POR_CONVERSA, ex);
            throw new DataProviderException(MENSAGEM_ERRO_LISTAR_POR_CONVERSA, ex.getCause());
        }

        return mensagens.stream().map(MensagemConversaMapper::paraDomain).toList();
    }
}
