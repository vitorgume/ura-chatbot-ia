package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.gateways.RelatorioGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioContatoDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.RelatorioOnlineDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.MensagemUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioUseCase {

    private final ClienteUseCase clienteUseCase;
    private final OutroContatoUseCase outroContatoUseCase;
    private final MensagemUseCase mensagemUseCase;
    private final RelatorioGateway gateway;

    @Scheduled(cron = "0 0 17 * * MON-FRI")
    public void enviarRelatorioDiarioVendedores() {
        log.info("Gerando relatório de contatos dos vendedores.");
        DayOfWeek dataHoje = LocalDate.now().getDayOfWeek();
        List<RelatorioContatoDto> relatorio;

        if(dataHoje.equals(DayOfWeek.MONDAY)) {
            relatorio = clienteUseCase.getRelatorioSegundaFeira();
        } else {
            relatorio = clienteUseCase.getRelatorio();
        }

        OutroContato gerencia = outroContatoUseCase.consultarPorNome("Ney");
        OutroContato consultor = outroContatoUseCase.consultarPorNome("Ricardo");

        String arquivo = gerarArquivo(relatorio);

        mensagemUseCase.enviarRelatorio(arquivo, "Relatorio.xlsx", gerencia.getTelefone());
        mensagemUseCase.enviarRelatorio(arquivo, "Relatorio.xlsx", consultor.getTelefone());

        log.info("Geração de relatório dos contatos dos vendedores concluida com sucesso.");
    }

    private String gerarArquivo(List<RelatorioContatoDto> contatos) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Contatos");

            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Nome");
            header.createCell(1).setCellValue("Telefone");
            header.createCell(2).setCellValue("Segmento");
            header.createCell(3).setCellValue("Região");
            header.createCell(4).setCellValue("Data de Criação");
            header.createCell(5).setCellValue("Vendedor");

            int rowNum = 1;
            for (RelatorioContatoDto dto : contatos) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(dto.getNome());
                row.createCell(1).setCellValue(dto.getTelefone());
                row.createCell(2).setCellValue(dto.getSegmento().getDescricao());
                row.createCell(3).setCellValue(dto.getRegiao().getDescricao());
                row.createCell(4).setCellValue(dto.getDataCriacao().toString());
                row.createCell(5).setCellValue(dto.getNomeVendedor());
            }

            for (int i = 0; i < 6; i++) {
                sheet.setColumnWidth(i, 6000);
            }

            byte[] planilha;

            try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                workbook.write(out);
                planilha = out.toByteArray();
            }

            return Base64.getEncoder().encodeToString(planilha);
        } catch (IOException ex) {
            log.error("Erro ao gerar relatório de vendedores", ex);
        }

        return "";
    }

    public void atualizarRelatorioOnline(Cliente cliente, Vendedor vendedor) {
        log.info("Atualizando relatório online. Cliente: {}, Vendedor: {}", cliente, vendedor);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        RelatorioOnlineDto novaLinha = RelatorioOnlineDto.builder()
                .data(LocalDate.now().format(formatter))
                .telefone(cliente.getTelefone())
                .cliente(cliente.getNome())
                .vendedor(vendedor.getNome())
                .build();

        gateway.atualizarRelatorioOnline(novaLinha);

        log.info("Relatório atualizado com sucesso.");
    }
}
