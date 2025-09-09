package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.RelatorioGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioOnlineDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioUseCaseTest {

    @Mock
    private ClienteUseCase clienteUseCase;

    @Mock
    private OutroContatoUseCase outroContatoUseCase;

    @Mock
    private MensagemUseCase mensagemUseCase;

    @Mock
    private RelatorioGateway gateway;

    @InjectMocks
    private RelatorioUseCase useCase;

    @Test
    void enviarRelatorioDiarioVendedores_deSegundaUsaRelatorioSegundaFeira() throws Exception {
        LocalDate fixedDate = LocalDate.of(2025, 7, 28);

        try (MockedStatic<LocalDate> mockDate =
                     mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockDate.when(LocalDate::now)
                    .thenReturn(fixedDate);

            LocalDateTime dt1 = LocalDateTime.of(2025,7,30,14,20);
            LocalDateTime dt2 = LocalDateTime.of(2025,7,31,9,15);

            RelatorioContatoDto r1 = RelatorioContatoDto.builder()
                    .nome("Ana")
                    .telefone("+55119991111")
                    .dataCriacao(dt1)
                    .nomeVendedor("João")
                    .segmento(Segmento.MEDICINA_SAUDE)
                    .regiao(Regiao.MARINGA)
                    .build();
            RelatorioContatoDto r2 = RelatorioContatoDto.builder()
                    .nome("Bruno")
                    .telefone("+55118882222")
                    .dataCriacao(dt2)
                    .nomeVendedor("Maria")
                    .segmento(Segmento.CELULARES)
                    .regiao(Regiao.OUTRA)
                    .build();
            List<RelatorioContatoDto> lista = List.of(r1, r2);

            when(clienteUseCase.getRelatorioSegundaFeira()).thenReturn(lista);

            OutroContato contato1 = mock(OutroContato.class);
            OutroContato contato2 = mock(OutroContato.class);
            when(outroContatoUseCase.consultarPorNome("Ney")).thenReturn(contato1);
            when(outroContatoUseCase.consultarPorNome("Ricardo")).thenReturn(contato2);
            when(contato1.getTelefone()).thenReturn("+55000000001");
            when(contato2.getTelefone()).thenReturn("+55000000002");

            useCase.enviarRelatorioDiarioVendedores();

            ArgumentCaptor<String> arquivoCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> fileNameCap = ArgumentCaptor.forClass(String.class);
            ArgumentCaptor<String> telefoneCap = ArgumentCaptor.forClass(String.class);
            verify(mensagemUseCase, times(2))
                    .enviarRelatorio(arquivoCap.capture(), fileNameCap.capture(), telefoneCap.capture());

            List<String> filenames = fileNameCap.getAllValues();
            assertEquals(2, filenames.size());
            assertEquals("Relatorio.xlsx", filenames.get(0));
            assertEquals("Relatorio.xlsx", filenames.get(1));

            List<String> telefones = telefoneCap.getAllValues();
            assertEquals("+55000000001", telefones.get(0));
            assertEquals("+55000000002", telefones.get(1));

            String base64 = arquivoCap.getValue();
            byte[] decoded = Base64.getDecoder().decode(base64);
            try (Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(decoded))) {
                Sheet sheet = wb.getSheet("Contatos");

                Row header = sheet.getRow(0);
                assertEquals("Nome",    header.getCell(0).getStringCellValue());
                assertEquals("Telefone",header.getCell(1).getStringCellValue());
                assertEquals("Segmento",header.getCell(2).getStringCellValue());
                assertEquals("Região",  header.getCell(3).getStringCellValue());
                assertEquals("Data de Criação", header.getCell(4).getStringCellValue());
                assertEquals("Vendedor", header.getCell(5).getStringCellValue());

                Row row1 = sheet.getRow(1);
                assertEquals("Ana",    row1.getCell(0).getStringCellValue());
                assertEquals("+55119991111", row1.getCell(1).getStringCellValue());
                assertEquals(r1.getSegmento().getDescricao(), row1.getCell(2).getStringCellValue());
                assertEquals(r1.getRegiao().getDescricao(),   row1.getCell(3).getStringCellValue());
                assertEquals(dt1.toString(),                  row1.getCell(4).getStringCellValue());
                assertEquals("João", row1.getCell(5).getStringCellValue());

                Row row2 = sheet.getRow(2);
                assertEquals("Bruno", row2.getCell(0).getStringCellValue());
                assertEquals("+55118882222", row2.getCell(1).getStringCellValue());
                assertEquals(r2.getSegmento().getDescricao(), row2.getCell(2).getStringCellValue());
                assertEquals(r2.getRegiao().getDescricao(),   row2.getCell(3).getStringCellValue());
                assertEquals(dt2.toString(),                  row2.getCell(4).getStringCellValue());
                assertEquals("Maria", row2.getCell(5).getStringCellValue());
            }
        }
    }

    @Test
    void enviarRelatorioDiarioVendedores_emDiaNaoSegundaUsaRelatorioNormal() {
        LocalDate fixedDate = LocalDate.of(2025, 7, 30); // quarta

        try (MockedStatic<LocalDate> mockDate =
                     mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockDate.when(LocalDate::now).thenReturn(fixedDate);

            RelatorioContatoDto dto = RelatorioContatoDto.builder()
                    .nome("Teste")
                    .telefone("+551234567890")
                    .segmento(Segmento.OUTROS)
                    .regiao(Regiao.OUTRA)
                    .dataCriacao(LocalDateTime.of(2025,7,30,9,0))
                    .nomeVendedor("Vendedor")
                    .build();
            when(clienteUseCase.getRelatorio()).thenReturn(List.of(dto));

            OutroContato c1 = mock(OutroContato.class);
            OutroContato c2 = mock(OutroContato.class);
            when(outroContatoUseCase.consultarPorNome("Ney")).thenReturn(c1);
            when(outroContatoUseCase.consultarPorNome("Ricardo")).thenReturn(c2);
            when(c1.getTelefone()).thenReturn("X1");
            when(c2.getTelefone()).thenReturn("X2");

            useCase.enviarRelatorioDiarioVendedores();

            verify(clienteUseCase).getRelatorio();
            verify(clienteUseCase, never()).getRelatorioSegundaFeira();
            verify(mensagemUseCase, times(2))
                    .enviarRelatorio(anyString(), eq("Relatorio.xlsx"), anyString());
        }
    }

    @Test
    void atualizarRelatorioOnline_montaDtoComDataFormatoBR_eChamaGateway() {
        LocalDate fixedDate = LocalDate.of(2025, 8, 5);

        try (MockedStatic<LocalDate> mockDate =
                     Mockito.mockStatic(LocalDate.class, Mockito.CALLS_REAL_METHODS)) {
            mockDate.when(LocalDate::now).thenReturn(fixedDate);

            Cliente cliente = mock(Cliente.class);
            Vendedor vendedor = mock(Vendedor.class);
            when(cliente.getTelefone()).thenReturn("+5544999887766");
            when(cliente.getNome()).thenReturn("Ana");
            when(vendedor.getNome()).thenReturn("João");

            useCase.atualizarRelatorioOnline(cliente, vendedor);

            ArgumentCaptor<RelatorioOnlineDto> dtoCap = ArgumentCaptor.forClass(RelatorioOnlineDto.class);
            verify(gateway, times(1)).atualizarRelatorioOnline(dtoCap.capture());
            verifyNoMoreInteractions(gateway);

            RelatorioOnlineDto dto = dtoCap.getValue();
            assertEquals("05/08/2025", dto.getData());
            assertEquals("+5544999887766", dto.getTelefone());
            assertEquals("Ana", dto.getCliente());
            assertEquals("João", dto.getVendedor());
        }
    }
}