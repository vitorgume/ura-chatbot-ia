package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensageriaGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Prioridade;
import com.guminteligencia.ura_chatbot_ia.domain.Regiao;
import com.guminteligencia.ura_chatbot_ia.domain.Segmento;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.VendedorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.VendedorEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.util.retry.RetryBackoffSpec;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(
        properties = {
                "spring.task.scheduling.enabled=false"
        }
)
@AutoConfigureMockMvc(addFilters = false)
@Tag("it")
class VendedorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VendedorRepository repository;

    @MockitoBean
    private MensageriaGateway mensageriaGateway;

    @MockitoBean
    private RetryBackoffSpec retryBackoffSpec;

    @BeforeEach
    void setMockMensageria() {
        given(mensageriaGateway.listarAvisos())
                .willReturn(List.of());
    }

    @Test
    void cadastrarQuandoSucessoRetornaCreated() throws Exception {
        given(repository.findByTelefone("99999999"))
                .willReturn(Optional.empty());

        VendedorEntity saved = new VendedorEntity();
        saved.setId(1L);
        saved.setNome("Pedro");
        saved.setTelefone("99999999");
        saved.setInativo(false);
        saved.setSegmentos(List.of(Segmento.MEDICINA_SAUDE));
        saved.setRegioes(List.of(Regiao.MARINGA));
        saved.setPrioridade(new Prioridade(1, true));
        given(repository.save(any(VendedorEntity.class)))
                .willReturn(saved);

        String json = """
            {
              "nome":"Pedro",
              "telefone":"99999999",
              "inativo":false,
              "segmentos":["MEDICINA_SAUDE"],
              "regioes":["MARINGA"],
              "prioridade": {
                    "valor": 1,
                    "prioritario": true
              }
            }
        """;

        mockMvc.perform(post("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/vendedores/1"))
                .andExpect(jsonPath("$.dado.id").value("1"))
                .andExpect(jsonPath("$.dado.nome").value("Pedro"))
                .andExpect(jsonPath("$.dado.telefone").value("99999999"))
                .andExpect(jsonPath("$.dado.inativo").value(false))
                .andExpect(jsonPath("$.dado.segmentos[0]").value("MEDICINA_SAUDE"))
                .andExpect(jsonPath("$.dado.regioes[0]").value("MARINGA"))
                .andExpect(jsonPath("$.dado.prioridade.valor").value(1))
                .andExpect(jsonPath("$.dado.prioridade.prioritario").value(true));
    }

    @Test
    void alterarQuandoSucessoRetornaOk() throws Exception {
        VendedorEntity existing = new VendedorEntity();
        existing.setId(2L);
        existing.setNome("A");
        existing.setTelefone("1111");
        existing.setInativo(false);
        existing.setSegmentos(List.of(Segmento.CELULARES));
        existing.setRegioes(List.of(Regiao.REGIAO_MARINGA));
        existing.setPrioridade(new Prioridade(1, true));
        given(repository.findById(2L))
                .willReturn(Optional.of(existing));

        VendedorEntity updated = new VendedorEntity();
        updated.setId(2L);
        updated.setNome("João");
        updated.setTelefone("88888888");
        updated.setInativo(true);
        updated.setSegmentos(List.of(Segmento.CELULARES));
        updated.setRegioes(List.of(Regiao.OUTRA));
        updated.setPrioridade(new Prioridade(1, true));
        given(repository.save(any(VendedorEntity.class)))
                .willReturn(updated);

        String json = """
            {
              "nome":"João",
              "telefone":"88888888",
              "inativo":true,
              "segmentos":["CELULARES"],
              "regioes":["REGIAO_MARINGA"],
              "prioridade": {
                    "valor": 1,
                    "prioritario": true
              }
            }
        """;

        mockMvc.perform(put("/vendedores/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado.id").value("2"))
                .andExpect(jsonPath("$.dado.nome").value("João"))
                .andExpect(jsonPath("$.dado.inativo").value(true))
                .andExpect(jsonPath("$.dado.segmentos[0]").value("CELULARES"))
                .andExpect(jsonPath("$.dado.regioes[0]").value("OUTRA"))
                .andExpect(jsonPath("$.dado.prioridade.valor").value(1))
                .andExpect(jsonPath("$.dado.prioridade.prioritario").value(true));;
    }

    @Test
    void listarQuandoSucessoRetornaOkComLista() throws Exception {
        VendedorEntity v1 = new VendedorEntity(null,"A","1111",false,
                List.of(Segmento.BOUTIQUE_LOJAS),
                List.of(Regiao.MARINGA),0, null);
        VendedorEntity v2 = new VendedorEntity(null,"B","2222",true,
                List.of(Segmento.CELULARES),
                List.of(Regiao.REGIAO_MARINGA), 0,    null);
        v1.setId(1L);
        v2.setId(2L);
        given(repository.findAll())
                .willReturn(List.of(v1, v2));

        mockMvc.perform(get("/vendedores")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dado[0].id").value("1"))
                .andExpect(jsonPath("$.dado[1].id").value("2"));
    }

    @Test
    void deletarQuandoSucessoRetornaNoContent() throws Exception {
        VendedorEntity existing = new VendedorEntity();
        existing.setId(2L);
        existing.setNome("A");
        existing.setTelefone("1111");
        existing.setInativo(false);
        existing.setSegmentos(List.of(Segmento.CELULARES));
        existing.setRegioes(List.of(Regiao.REGIAO_MARINGA));
        existing.setPrioridade(new Prioridade(1, true));

        given(repository.findById(Mockito.any()))
                .willReturn(Optional.of(existing));

        mockMvc.perform(delete("/vendedores/3"))
                .andExpect(status().isNoContent());

        then(repository).should().deleteById(3L);
    }

    @Test
    void deletarQuandoNaoEncontradoRetornaInternalServerError() throws Exception {
        VendedorEntity dummy = new VendedorEntity();
        dummy.setId(4L);
        given(repository.findById(4L)).willReturn(Optional.of(dummy));

        willThrow(new RuntimeException("falha"))
                .given(repository).deleteById(4L);

        mockMvc.perform(delete("/vendedores/4"))
                .andExpect(status().isInternalServerError());
    }
}