package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MensagemGateway;
import com.guminteligencia.ura_chatbot_ia.domain.OutroContato;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens.MensagemBuilder;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import com.guminteligencia.ura_chatbot_ia.domain.Vendedor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensagemUseCase {

    private final MensagemGateway gateway;
    private final MensagemBuilder mensagemBuilder;

    public void enviarMensagem(String mensagem, String telefone, boolean semEspacos) {
        log.info("Enviando mensagem para usuário. Resposta: {}, Telefone: {}", mensagem, telefone);

        String mensagemAEnviar = semEspacos ? mensagem.replaceAll("^\"|\"$", "").replaceAll("\\n", "").replaceAll("\\n\\n", "") : mensagem.replaceAll("^\"|\"$", "");

        this.gateway.enviar(mensagemAEnviar, telefone);

        log.info("Mensagem para o usuário enviada com sucesso.");
    }

    public void enviarContatoVendedor(Vendedor vendedor, Cliente cliente) {
        log.info("Enviando contato para vendedor. Vendedor: {}, Cliente: {}", vendedor, cliente);

        String textoMensagem = mensagemBuilder.getMensagem(TipoMensagem.DADOS_CONTATO_VENDEDOR, null, cliente);
        String textoSeparacao = mensagemBuilder.getMensagem(TipoMensagem.MENSAGEM_SEPARACAO, null, null);

        gateway.enviarContato(vendedor.getTelefone(), cliente);

        this.enviarMensagem(textoMensagem, vendedor.getTelefone(), false);
        this.enviarMensagem(textoSeparacao, vendedor.getTelefone(), false);

        log.info("Contato enviado com sucesso para vendedor.");
    }

    public void enviarRelatorio(String arquivo, String fileName, String telefone) {
        log.info("Enviando relatório de vendedores.");
        gateway.enviarRelatorio(arquivo, fileName, telefone);
        log.info("Relatório enviado com sucesso.");
    }
}
