package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.CrmGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.CardDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.dto.ContactsResponse;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Comparator;
import java.util.Optional;

@Component
@Slf4j
public class CrmDataProvider implements CrmGateway {
    private static final String KOMMO_DRIVE_BASE = "https://drive-c.kommo.com";

    private final WebClient webClient;

    public CrmDataProvider(@Qualifier("kommoWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public static final String MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE = "Erro ao consultar lead pelo seu telefone.";
    public static final String MENSAGEM_ERRO_ATUALIZAR_CARD = "Erro ao atualizar card.";


    public Optional<Integer> consultaLeadPeloTelefone(String telefoneE164) {
        String normalized = normalizeE164(telefoneE164);

        Integer leadId;

        try {
            ContactsResponse contacts = webClient.get()
                    .uri(uri -> uri.path("/contacts")
                            .queryParam("query", normalized)
                            .queryParam("with", "leads")
                            .queryParam("limit", 50)
                            .build())
                    .retrieve()
                    .bodyToMono(ContactsResponse.class)
                    .block();

            if (contacts == null || contacts.getEmbedded() == null || contacts.getEmbedded().getContacts() == null) {
                return Optional.empty();
            }

            var contato = contacts.getEmbedded().getContacts().stream()
                    .filter(c -> c.getEmbedded() != null && c.getEmbedded().getLeads() != null && !c.getEmbedded().getLeads().isEmpty())
                    .max(Comparator.comparing(ContactDto::getUpdatedAt, Comparator.nullsFirst(Long::compareTo)))
                    .orElse(null);

            if (contato == null) return Optional.empty();

            leadId = contato.getEmbedded().getLeads().get(0).getId();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_LEAD_PELO_TELEFONE, ex.getCause());
        }

        return Optional.ofNullable(leadId);
    }

    @Override
    public void atualizarCard(CardDto body, Integer idLead) {
        try {
            webClient.patch()
                    .uri(uri -> uri.path("/leads/{id}").build(idLead))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(body)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_ATUALIZAR_CARD, ex);
            throw new DataProviderException(MENSAGEM_ERRO_ATUALIZAR_CARD, ex.getCause());
        }
    }

    private static String normalizeE164(String fone) {
        String f = fone == null ? "" : fone.trim();
        f = f.replaceAll("[^\\d+]", "");
        if (!f.startsWith("+")) f = "+" + f;
        return f;
    }
}
