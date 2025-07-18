package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MensagemAlertaRecontato implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();
        LocalDateTime dataHoje = LocalDateTime.now();

        String horaMinutos = String.format("%02d:%02d", dataHoje.getHour(), dataHoje.getMinute());

        mensagem.append("Cliente fez um recontato:\n");
        mensagem.append("Cliente: ").append(cliente.getNome()).append("\n");
        mensagem.append("Vendedor: ").append(nomeVendedor).append("\n");
        mensagem.append("Hora: ").append(horaMinutos);

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_ALERTA_RECONTATO.getCodigo();
    }
}
