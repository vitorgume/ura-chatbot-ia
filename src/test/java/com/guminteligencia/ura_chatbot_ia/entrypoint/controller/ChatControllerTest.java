package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.usecase.ChatUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
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