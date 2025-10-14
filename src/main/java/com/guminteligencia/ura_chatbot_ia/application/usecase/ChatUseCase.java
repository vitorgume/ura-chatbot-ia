package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.ChatNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.ChatGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MensagemConversa;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class ChatUseCase {

    private final ChatGateway gateway;
    private final MensagemConversaUseCase mensagemConversaUseCase;
    private final ConversaAgenteUseCase conversaAgenteUseCase;
    private final String URL_CHAT;

    public ChatUseCase(
            ChatGateway chatGateway,
            MensagemConversaUseCase mensagemConversaUseCase,
            ConversaAgenteUseCase conversaAgenteUseCase,
            @Value("${neprint.url.chat}") String URL_CHAT
    ) {
        this.gateway = chatGateway;
        this.mensagemConversaUseCase = mensagemConversaUseCase;
        this.conversaAgenteUseCase = conversaAgenteUseCase;
        this.URL_CHAT = URL_CHAT;
    }

    public Chat acessar(UUID idChat) {
        Optional<Chat> chat = gateway.consultarPorId(idChat);

        if(chat.isEmpty()) {
            throw new ChatNaoEncontradoException();
        }

        return chat.get();
    }

    public String criar(UUID idConversa) {
        log.info("Criando novo chat. Id da conversa: {}", idConversa);

        ConversaAgente conversaAgente = conversaAgenteUseCase.consultarPorId(idConversa);
        List<MensagemConversa> mensagens = mensagemConversaUseCase.listarPelaConversa(idConversa);

        Chat chat = Chat.builder()
                .dataCriacao(LocalDateTime.now())
                .cliente(conversaAgente.getCliente())
                .mensagensChat(mensagens)
                .build();

        chat = gateway.salvar(chat);

        log.info("Chat criado com sucesso. Chat: {}", chat);

        return URL_CHAT + chat.getId().toString();
    }
}
