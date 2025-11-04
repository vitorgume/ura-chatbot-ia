package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.ChatNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ChatUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ChatDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ChatMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatUseCase chatUseCase;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRetornarChatPeloId() throws Exception {
        Mockito.when(chatUseCase.acessar(Mockito.any())).thenReturn(
                Chat.builder()
                        .id(UUID.fromString("7694f680-c639-4519-a65c-b7fe01f284b8"))
                        .cliente(Cliente.builder().id(UUID.randomUUID()).build())
                        .mensagensChat(List.of())
                        .build()
        );

        mockMvc.perform(get("/chats/" + UUID.randomUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value("7694f680-c639-4519-a65c-b7fe01f284b8"));

        Mockito.verify(chatUseCase).acessar(Mockito.any());
    }


}