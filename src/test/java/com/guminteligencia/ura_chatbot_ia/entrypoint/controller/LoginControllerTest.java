package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.CredenciasIncorretasException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.LoginUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginUseCase loginUseCase;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @BeforeEach
    void setMockMensageria() {
        given(mensageriaGateway.listarMensagens())
                .willReturn(List.of());
    }

    @Test
    void logarComCredenciaisValidasRetornaCreatedEBodyCorreto() throws Exception {
        String payload = """
                    { "telefone": "+5511999000111", "senha": "senha123" }
                """;

        UUID esperadoId = UUID.randomUUID();
        var domainResponse = LoginResponse.builder()
                .token("meu-token-abc")
                .id(esperadoId)
                .build();

        given(loginUseCase.autenticar("+5511999000111", "senha123"))
                .willReturn(domainResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dado.token").value("meu-token-abc"))
                .andExpect(jsonPath("$.dado.id").value(esperadoId.toString()));
    }


    @Test
    void logarComCredenciaisInvalidasRetornaUnauthorized() throws Exception {
        String telefone = "+5511999000111";
        String payload = """
                    { "telefone": "%s", "senha": "senhaErrada" }
                """.formatted(telefone);

        given(loginUseCase.autenticar(telefone, "senhaErrada"))
                .willThrow(new CredenciasIncorretasException());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }


}