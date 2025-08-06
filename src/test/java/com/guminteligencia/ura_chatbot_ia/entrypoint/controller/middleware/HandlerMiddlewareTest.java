package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.middleware;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.*;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
class HandlerMiddlewareTest {
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        TestController ctrl = new TestController();
        HandlerMiddleware advice = new HandlerMiddleware();
        mockMvc = MockMvcBuilders.standaloneSetup(ctrl)
                .setControllerAdvice(advice)
                .build();
    }

    @RestController
    static class TestController {
        @GetMapping("/generic")
        public void generic() {
            throw new RuntimeException("generic error");
        }

        @GetMapping("/adminExists")
        public void adminExists() {
            throw new AdministradorJaExisteException();
        }

        @GetMapping("/adminNotFound")
        public void adminNotFound() {
            throw new AdministradorNaoEncontradoException();
        }

        @GetMapping("/badCreds")
        public void badCreds() {
            throw new CredenciasIncorretasException();
        }

        @GetMapping("/clienteNotFound")
        public void clienteNotFound() {
            throw new ClienteNaoEncontradoException();
        }

        @GetMapping("/contextoNotFound")
        public void contextoNotFound() {
            throw new ContextoNaoEncontradoException();
        }

        @GetMapping("/conversaAgenteNotFound")
        public void conversaAgenteNotFound() {
            throw new ConversaAgenteNaoEncontradoException();
        }

        @GetMapping("/escolhaNaoIdentificado")
        public void escolhaNaoIdentificado() {
            throw new EscolhaNaoIdentificadoException();
        }

        @GetMapping("/processoExistenteNaoIdentificado")
        public void processoExistenteNaoIdentificado() {
            throw new ProcessoContextoExistenteNaoIdentificadoException();
        }

        @GetMapping("/vendedorSamePhone")
        public void vendedorSamePhone() {
            throw new VendedorComMesmoTelefoneException();
        }

        @GetMapping("/vendedorNotFound")
        public void vendedorNotFound() {
            throw new VendedorNaoEncontradoException();
        }

        @GetMapping("/vendedorNotChosen")
        public void vendedorNotChosen() {
            throw new VendedorNaoEscolhidoException();
        }

        @GetMapping("/dataProvider")
        public void dataProvider() {
            throw new DataProviderException("DP error", null);
        }

        @GetMapping("/outroContatoNotFound")
        public void outroContatoNotFound() {
            throw new OutroContatoNaoEncontradoException();
        }

        @GetMapping("/nullMessage")
        public void nullMessage() {
            throw new RuntimeException();
        }
    }

    @ParameterizedTest(name = "{0} → {1} / “{2}”")
    @CsvSource({
            "generic,                      500, generic error",
            "adminExists,                  400, Administrador já cadastrado com esse email.",
            "adminNotFound,                404, Administrador não encontrado.",
            "badCreds,                     401, Credências incorretas.",
            "clienteNotFound,              404, Cliente não encontrado.",
            "contextoNotFound,             404, Contexto não encontrado.",
            "conversaAgenteNotFound,       404, Conversa não econtrada.",
            "escolhaNaoIdentificado,       500, Escolha de vendedor não identificada",
            "processoExistenteNaoIdentificado, 500, Processo de contexto existente não foi identificado.",
            "vendedorSamePhone,            400, Vendedor com mesmo numero de telefone já cadastrado",
            "vendedorNotFound,             404, Vendedor não encontrado.",
            "vendedorNotChosen,            500, Vendedor não escolhido de acordo com segmentação.",
            "dataProvider,                 500, DP error",
            "outroContatoNotFound,         404, Outro contato não encontrado.",
            "nullMessage,                  500, Erro interno inesperado."
    })
    void testHandlerMiddleware(
            String path,
            int expectedStatus,
            String expectedMessage
    ) throws Exception {
        mockMvc.perform(get("/" + path)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(expectedStatus))
                .andExpect(jsonPath("$.dado").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.erro.mensagens[0]").value(expectedMessage));
    }
}