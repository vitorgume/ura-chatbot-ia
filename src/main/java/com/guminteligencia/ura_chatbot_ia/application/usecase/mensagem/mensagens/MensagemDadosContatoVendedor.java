package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MensagemDadosContatoVendedor implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();
        LocalDateTime dataHoje = LocalDateTime.now();

        String horaMinutos = String.format("%02d:%02d", dataHoje.getHour(), dataHoje.getMinute());

        mensagem.append("Dados do contato acima:\n");

        if(cliente.getNome() != null) {
            mensagem.append("Nome: ").append(cliente.getNome()).append("\n");
        } else {
            mensagem.append("Nome: ").append("Nome não informado").append("\n");
        }

        if(cliente.getSegmento() != null) {
            mensagem.append("Segmento: ").append(cliente.getSegmento().getDescricao()).append("\n");
        } else {
            mensagem.append("Segmento: ").append("Segmento não informado").append("\n");
        }

        mensagem.append("Hora: ").append(horaMinutos).append("\n");

        if(cliente.getRegiao() != null) {
            mensagem.append("Região: ").append(cliente.getRegiao().getDescricao()).append("\n");
        } else {
            mensagem.append("Região: ").append("Região não informada").append("\n");
        }

        if(cliente.getTelefone() != null) {
            mensagem.append("Telefone: ").append(cliente.getTelefone()).append("\n");
        } else {
            mensagem.append("Telefone: ").append("Telefone não informado").append("\n");
        }

        if(cliente.getDescricaoMaterial() != null) {
            mensagem.append("Descrição material: ").append(cliente.getDescricaoMaterial());
        } else {
            mensagem.append("Descrição material: ").append("Descrição material não informado.");
        }

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.DADOS_CONTATO_VENDEDOR.getCodigo();
    }
}
