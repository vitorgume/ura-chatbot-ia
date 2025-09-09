package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioOnlineDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem.WebClientExecutor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioDataProviderTest {

    @Mock
    private WebClientExecutor executor;

    private RelatorioDataProvider providerProd;
    private RelatorioDataProvider providerDev;

    private final String uri = "https://relatorio.exemplo/api";

    private PrintStream originalOut;
    private ByteArrayOutputStream outContent;

    @BeforeEach
    void setup() {
        providerProd = new RelatorioDataProvider(executor, uri, "prod");
        providerDev  = new RelatorioDataProvider(executor, uri, "dev");

        originalOut = System.out;
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
    }

    @Test
    void atualizarRelatorioOnline_emProd_chamaExecutorPost() {
        RelatorioOnlineDto dto = RelatorioOnlineDto.builder()
                .data("05/08/2025")
                .telefone("+5544999887766")
                .cliente("Ana")
                .vendedor("João")
                .build();

        providerProd.atualizarRelatorioOnline(dto);

        verify(executor, times(1))
                .post(eq(uri), eq(dto), eq(Map.of()), eq("Erro ao atualizar relatório online."));
        verifyNoMoreInteractions(executor);
    }

    @Test
    void atualizarRelatorioOnline_emNaoProd_naoChamaExecutor_eLogaNoConsole() {
        RelatorioOnlineDto dto = RelatorioOnlineDto.builder()
                .data("05/08/2025")
                .telefone("+5544999887766")
                .cliente("Ana")
                .vendedor("João")
                .build();

        providerDev.atualizarRelatorioOnline(dto);

        verifyNoInteractions(executor);

        String console = outContent.toString();
        assertTrue(
                console.contains("Relatório online atualizado. Nova linha: "),
                "Deveria ter impresso mensagem de atualização no console em ambiente não-prod"
        );
    }
}