package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.CriptografiaUseCase;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.AdministradorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.AdministradorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdministradorRepository repository;

    @MockitoBean
    private CriptografiaUseCase criptografiaUseCase;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @BeforeEach
    void setMockMensageria() {
        given(mensageriaGateway.listarMensagens())
                .willReturn(List.of());
    }

    @Test
    void cadastrarComSucessoRetornaCreated() throws Exception {
        String payload = """
            {
              "nome": "Admin Teste",
              "email": "admin@teste.com",
              "senha": "senha123"
            }
        """;

        given(repository.findByEmail("admin@teste.com"))
                .willReturn(Optional.empty());

        given(criptografiaUseCase.criptografar("senha123"))
                .willReturn("HASHED_pwd123");

        UUID esperadoId = UUID.randomUUID();
        AdministradorEntity saved = new AdministradorEntity();
        saved.setId(esperadoId);
        saved.setNome("Admin Teste");
        saved.setEmail("admin@teste.com");
        saved.setSenha("HASHED_pwd123");
        given(repository.save(any(AdministradorEntity.class)))
                .willReturn(saved);

        mockMvc.perform(post("/administradores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/administradores/" + esperadoId))
                .andExpect(jsonPath("$.dado.id").value(esperadoId.toString()))
                .andExpect(jsonPath("$.dado.nome").value("Admin Teste"))
                .andExpect(jsonPath("$.dado.email").value("admin@teste.com"));

        then(repository).should().findByEmail("admin@teste.com");
        then(criptografiaUseCase).should().criptografar("senha123");
        then(repository).should().save(any(AdministradorEntity.class));
    }

    @Test
    void deletarComSucessoRetornaNoContent() throws Exception {
        UUID id = UUID.randomUUID();
        willDoNothing().given(repository).deleteById(id);

        mockMvc.perform(delete("/administradores/{id}", id))
                .andExpect(status().isNoContent());

        then(repository).should().deleteById(id);
    }
}