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
    void logar_comCredenciaisValidas_retornaCreatedEBodyCorreto() throws Exception {
        String payload = """
                    {
                      "email": "admin@teste.com",
                      "senha": "senha123"
                    }
                """;

        UUID esperadoId = UUID.randomUUID();
        LoginResponse domainResponse = LoginResponse.builder()
                .token("meu-token-abc")
                .id(esperadoId)
                .build();
        given(loginUseCase.autenticar("admin@teste.com", "senha123"))
                .willReturn(domainResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dado.token").value("meu-token-abc"))
                .andExpect(jsonPath("$.dado.id").value(esperadoId.toString()));
    }

    @Test
    void logar_comCredenciaisInvalidas_retornaUnauthorized() throws Exception {
        String payload = """
                    {
                      "email": "admin@teste.com",
                      "senha": "senhaErrada"
                    }
                """;

        given(loginUseCase.autenticar("admin@teste.com", "senhaErrada"))
                .willThrow(new CredenciasIncorretasException());

        // Esperamos 401 Unauthorized
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnauthorized());
    }


}