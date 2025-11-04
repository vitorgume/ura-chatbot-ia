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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.util.retry.RetryBackoffSpec;

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
@ActiveProfiles("test")
class AdministradorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdministradorRepository repository;

    @MockitoBean
    private CriptografiaUseCase criptografiaUseCase;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @MockitoBean
    private RetryBackoffSpec retryBackoffSpec;

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
              "telefone": "0000000000000",
              "senha": "senha123"
            }
        """;

        given(repository.findByTelefone("0000000000000"))
                .willReturn(Optional.empty());

        given(criptografiaUseCase.criptografar("senha123"))
                .willReturn("HASHED_pwd123");

        UUID esperadoId = UUID.randomUUID();
        AdministradorEntity saved = new AdministradorEntity();
        saved.setId(esperadoId);
        saved.setNome("Admin Teste");
        saved.setTelefone("0000000000000");
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
                .andExpect(jsonPath("$.dado.telefone").value("0000000000000"));

        then(repository).should().findByTelefone("0000000000000");
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