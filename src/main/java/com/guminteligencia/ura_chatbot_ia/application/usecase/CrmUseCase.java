package com.guminteligencia.ura_chatbot_ia.application.usecase;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.LeadNaoEncontradoException;
import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CustomFieldDto;
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
        String urlChat = chatUseCase.criar(conversaAgente.getId());
        Integer idLead = this.consultaLeadPeloTelefone(cliente.getTelefone());

        List<CustomFieldDto> customFieldDtos = new ArrayList<>();

        Map<String, Object> descricaoMaterias = Map.of("value", cliente.getDescricaoMaterial());
        Map<String, Object> segmento = Map.of("enum_id", cliente.getSegmento().getIdCrm());
        Map<String, Object> regioe = Map.of("enum_id", cliente.getRegiao().getIdCrm());
        Map<String, Object> regiaoReal = Map.of("values", cliente.getRegiaoReal());
        Map<String, Object> urlHistorico = Map.of("values", urlChat);


        customFieldDtos.add(CustomFieldDto.builder().fieldId(1484843).values(List.of(descricaoMaterias)).build());
        customFieldDtos.add(CustomFieldDto.builder().fieldId(1486843).values(List.of(segmento)).build());
        customFieldDtos.add(CustomFieldDto.builder().fieldId(1486845).values(List.of(regioe)).build());
        customFieldDtos.add(CustomFieldDto.builder().fieldId(1486847).values(List.of(regiaoReal)).build());
        customFieldDtos.add(CustomFieldDto.builder().fieldId(1486849).values(List.of(urlHistorico)).build());

        Map<String, Integer> tagItem = cliente.isInativo() ? Map.of("id", 111143) : Map.of("id", 117527);

        Map<String, List<Map<String, Integer>>> tags = Map.of("tags", List.of(tagItem));

        CardDto cardDto = CardDto.builder()
                .responsibleUseId(vendedor.getIdVendedorCrm())
                .customFieldsValues(customFieldDtos)
                .statusId(93572343)
                .embedded(tags)
                .build();

        gateway.atualizarCard(cardDto, idLead);

    }

    public Integer consultaLeadPeloTelefone(String telefone) {
        Optional<Integer> lead = gateway.consultaLeadPeloTelefone(telefone);

        if(lead.isEmpty()) {
            throw new LeadNaoEncontradoException();
        }

        return lead.get();
    }

    public void carregarArquivo(String urlArquivo) {
        SessaoArquivoDto sessaoArquivo = gateway.criarSessaoArquivo();
        String result = gateway.enviarArquivoParaUpload(sessaoArquivo, urlArquivo);

    }
}
