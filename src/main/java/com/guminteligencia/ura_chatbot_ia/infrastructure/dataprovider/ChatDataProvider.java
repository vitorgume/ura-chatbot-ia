package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.ChatGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.ChatMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.ChatRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.ChatEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatDataProvider implements ChatGateway {

    private final ChatRepository repository;

    private final String MENSAGEM_ERRO_CONSULTAR_POR_ID = "Erro ao consultar chat pelo seu id.";
    private final String MENSAMGE_ERRO_SALVAR = "Erro ao salvar chat.";

    @Override
    public Optional<Chat> consultarPorId(UUID idChat) {
        Optional<ChatEntity> chatEntity;

        try {
            chatEntity = repository.findById(idChat);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_ID, ex.getCause());
        }

        return chatEntity.map(ChatMapper::paraDomain);
    }

    @Override
    public Chat salvar(Chat chat) {
        ChatEntity chatEntity = ChatMapper.paraEntity(chat);

        try {
            chatEntity = repository.save(chatEntity);
        } catch (Exception ex) {
            log.error(MENSAMGE_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAMGE_ERRO_SALVAR, ex.getCause());
        }

        return ChatMapper.paraDomain(chatEntity);
    }
}
