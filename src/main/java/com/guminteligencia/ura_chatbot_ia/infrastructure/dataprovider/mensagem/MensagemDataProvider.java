package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.ContatoRequestDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.DocumentoRequestDto;
import com.guminteligencia.ura_chatbot_ia.application.usecase.dto.MensagemRequestWhatsAppDto;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MensagemDataProvider implements MensagemGateway {

    private final WebClientExecutor executor;

    @Value("${neoprint.ura.whatsapp.token}")
    private final String token;

    @Value("${neoprint.ura.whatsapp.id-instance}")
    private final String idInstance;

    @Value("${neoprint.ura.whatsapp.client-token}")
    private final String clienteToken;

    @Value("${spring.profiles.active}")
    private final String profile;

    public MensagemDataProvider(
            WebClientExecutor executor,
            @Value("${neoprint.ura.whatsapp.token}") String token,
            @Value("${neoprint.ura.whatsapp.id-instance}") String idInstance,
            @Value("${neoprint.ura.whatsapp.client-token}") String clienteToken,
            @Value("${spring.profiles.active}") String profile
    ) {
        this.executor = executor;
        this.token = token;
        this.idInstance = idInstance;
        this.clienteToken = clienteToken;
        this.profile = profile;
    }

    @Override
    public void enviar(String resposta, String telefone) {
        MensagemRequestWhatsAppDto body = MensagemRequestWhatsAppDto.builder()
                .phone(telefone)
                .message(resposta)
                .build();

        if (profile.equals("prod")) {
            Map<String, String> headers = Map.of("Client-Token", clienteToken);

            String uri = String.format("https://api.z-api.io/instances/%s/token/%s/send-text", idInstance, token);

            executor.post(uri, body, headers, "Erro ao enviar mensagem.");
        } else {
            System.out.println("Mensagem enviada: " + body);
        }
    }

    @Override
    public void enviarContato(String telefoneReceptivo, String telefoneContato, String nomeContato) {
        String nomeContatoOficial = nomeContato != null ? nomeContato : "Nome não informado";

        ContatoRequestDto body = ContatoRequestDto.builder()
                .phone(telefoneReceptivo)
                .contactName(nomeContatoOficial)
                .contactPhone(telefoneContato)
                .build();

        if(profile.equals("prod")) {
            Map<String, String> headers = Map.of("Client-Token", clienteToken);

            String uri = String.format("https://api.z-api.io/instances/%s/token/%s/send-contact", idInstance, token);

            executor.post(uri, body, headers, "Erro ao enviar contato.");
        } else {
            System.out.println("Contato enviado: " + body);
        }
    }

    @Override
    public void enviarRelatorio(String arquivo, String fileName, String telefone) {
        String base64ComPrefixo = "data:application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;base64," + arquivo;
        DocumentoRequestDto body = new DocumentoRequestDto(telefone, base64ComPrefixo, fileName);

        if(profile.equals("prod")) {
            Map<String, String> headers = Map.of("Client-Token", clienteToken);

            String uri = String.format("/instances/%s/token/%s/send-document/xlsx", idInstance, token);

            executor.post(uri, body, headers, "Erro ao enviar relatório.");
        } else {
            System.out.println("Enviado relatório: " + body);
        }
    }
}
