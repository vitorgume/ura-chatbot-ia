package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldValueDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CrmUseCase {

    private final CrmGateway gateway;
    private final ChatUseCase chatUseCase;
    private final MidiaClienteUseCase midiaClienteUseCase;

    public void atualizarCrm(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {
        log.info("Atualizando crm. Vendedor: {}, Cliente: {}, Conversa: {}", vendedor, cliente, conversaAgente);

        String urlChat = chatUseCase.criar(conversaAgente.getId());
        Integer idLead = this.consultaLeadPeloTelefone(cliente.getTelefone());

        // AJUSTE: value para campos texto; enum_id para selects
        List<CustomFieldDto> customFieldDtos = new ArrayList<>();

        // Ex.: 1484843 = descricao_material (texto)
        customFieldDtos.add(textField(1484843, cliente.getDescricaoMaterial()));

        // Ex.: 1486843 = segmento (SELECT) -> usa enum_id
        customFieldDtos.add(selectField(1486843, cliente.getSegmento().getIdCrm()));

        // Ex.: 1486845 = regiao (SELECT) -> usa enum_id
        customFieldDtos.add(selectField(1486845, cliente.getRegiao().getIdCrm()));

        // Ex.: 1486847 = endereco_real (texto) -> usa value (**NÃO** "values")
        customFieldDtos.add(textField(1486847, cliente.getEnderecoReal()));

        // Ex.: 1486849 = url_historico (texto)
        customFieldDtos.add(textField(1486849, urlChat));

        // Tags no _embedded
        Map<String, Integer> tagItem = cliente.isInativo()
                ? Map.of("id", 111143)
                : Map.of("id", 117527);

        Map<String, Object> embedded = Map.of("tags", List.of(tagItem));

        midiaClienteUseCase.consultarMidiaPeloTelefoneCliente(cliente.getTelefone()).ifPresent(midia -> midia.getUrlMidias().forEach(this::carregarArquivo));

        CardDto cardDto = CardDto.builder()
                .responsibleUserId(vendedor.getIdVendedorCrm()) // responsible_user_id
                .statusId(93572343)                              // status_id
                .customFieldsValues(customFieldDtos)             // custom_fields_values
                .embedded(embedded)                              // _embedded
                .build();

        // (opcional) logar o JSON real antes de enviar
        try {
            var json = new com.fasterxml.jackson.databind.ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(cardDto);
            log.info("Kommo PATCH body=\n{}", json);
        } catch (Exception ignore) {
        }

        gateway.atualizarCard(cardDto, idLead);

        log.info("Atualização do crm concluída com sucesso. Card: {}, Id do lead: {}", cardDto, idLead);
    }

    public Integer consultaLeadPeloTelefone(String telefone) {
        Optional<Integer> lead = gateway.consultaLeadPeloTelefone(telefone);

        if (lead.isEmpty()) {
            throw new LeadNaoEncontradoException();
        }

        return lead.get();
    }

    public void carregarArquivo(String urlArquivo) {
        SessaoArquivoDto sessaoArquivo = gateway.criarSessaoArquivo();
        gateway.enviarArquivoParaUpload(sessaoArquivo, urlArquivo);
    }

    private CustomFieldDto textField(int fieldId, Object value) {
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(List.of(CustomFieldValueDto.builder()
                        .value(value)       // só value
                        .build()))
                .build();
    }

    private CustomFieldDto selectField(int fieldId, Integer... enumIds) {
        var list = java.util.Arrays.stream(enumIds)
                .map(id -> CustomFieldValueDto.builder()
                        .enumId(id)         // só enum_id
                        .build())
                .toList();
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(list)
                .build();
    }
}
