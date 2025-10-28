package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldValueDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.SessaoArquivoDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
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

    public void atualizarCrm(Vendedor vendedor, Cliente cliente, ConversaAgente conversaAgente) {
        log.info("Atualizando crm. Vendedor: {}, Cliente: {}, Conversa: {}", vendedor, cliente, conversaAgente);

        String urlChat = chatUseCase.criar(conversaAgente.getId());
        Integer idLead = this.consultaLeadPeloTelefone(cliente.getTelefone());

        log.info("Construindo body para atualizar card.");

        List<CustomFieldDto> customFieldDtos = new ArrayList<>();

        addTextIfPresent(customFieldDtos, 1484843, cliente.getDescricaoMaterial());

        customFieldDtos.add(selectField(1486843, cliente.getSegmento() == null ? 1242461 : cliente.getSegmento().getIdCrm()));

        customFieldDtos.add(selectField(1486845, cliente.getRegiao() == null ? 1242469 : cliente.getRegiao().getIdCrm()));

        addTextIfPresent(customFieldDtos, 1486847, cliente.getEnderecoReal());

        addTextIfPresent(customFieldDtos, 1486849, urlChat);

        Map<String, Integer> tagItem = conversaAgente.getInativo() == null || conversaAgente.getInativo().getCodigo().equals(0)
                ? Map.of("id", 117527)
                : Map.of("id", 111143);

        Integer statusId = conversaAgente.getInativo().getCodigo().equals(1) ? 95198915 : 93572343;

        Map<String, Integer> tagIdentificador = Map.of("id", 126472);

        Map<String, Object> embedded = Map.of("tags", List.of(tagItem, tagIdentificador));

        CardDto cardDto = CardDto.builder()
                .responsibleUserId(vendedor.getIdVendedorCrm())
                .statusId(statusId)
                .customFieldsValues(customFieldDtos)
                .embedded(embedded)
                .build();

        log.info("Body para atualizar card criado com sucesso. Body: {}", cardDto);

        try {
            var json = new ObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(cardDto);
            log.info("Kommo PATCH body=\n{}", json);
        } catch (Exception ignore) {
        }

        gateway.atualizarCard(cardDto, idLead);

        log.info("Atualização do crm concluída com sucesso. Card: {}, Id do lead: {}", cardDto, idLead);
    }

    public Integer consultaLeadPeloTelefone(String telefone) {
        log.info("Consultando lead pelo telefone. Telefone: {}", telefone);
        Optional<Integer> lead = gateway.consultaLeadPeloTelefone(telefone);

        if(lead.isEmpty()) {
            throw new LeadNaoEncontradoException();
        }

        log.info("Lead consultado com sucesso. Lead: {}", lead.get());
        return lead.get();
    }

    private CustomFieldDto textField(int fieldId, Object value) {
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(List.of(CustomFieldValueDto.builder()
                        .value(value)
                        .build()))
                .build();
    }

    private CustomFieldDto selectField(int fieldId, Integer... enumIds) {
        var list = java.util.Arrays.stream(enumIds)
                .map(id -> CustomFieldValueDto.builder()
                        .enumId(id)
                        .build())
                .toList();
        return CustomFieldDto.builder()
                .fieldId(fieldId)
                .values(list)
                .build();
    }

    private void addTextIfPresent(List<CustomFieldDto> list, int fieldId, String value) {
        if (value != null && !value.isBlank()) {
            list.add(textField(fieldId, value));
        }
    }
}
