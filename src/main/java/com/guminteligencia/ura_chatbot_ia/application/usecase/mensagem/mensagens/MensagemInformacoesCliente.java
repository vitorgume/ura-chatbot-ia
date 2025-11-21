package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemInformacoesCliente implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        StringBuilder mensagem = new StringBuilder();

        mensagem.append("Perfeito! Já encaminhei seu atendimento para o especialista ideal.").append("\n");
        mensagem.append("Enquanto isso, para agilizar todo o processo e garantir que você receba um orçamento mais rápido e preciso, pode separar algumas informações?").append("\n");
        mensagem.append("📍 Informações importantes para ter em mãos:").append("\n");
        mensagem.append("- Logo da sua empresa").append("\n");
        mensagem.append("- Medidas exatas do que você precisa").append("\n");
        mensagem.append("- Se esse item já foi produzido antes (e por quem)").append("\n");
        mensagem.append("- Uma foto de referência (pode ser do ambiente, da peça ou de algo similar)").append("\n");
        mensagem.append("Esses detalhes ajudam nosso time a entender exatamente o que você precisa e acelerar o seu atendimento 😉").append("\n");
        mensagem.append("Fique no aguardo — o especialista já está chegando!");

        return mensagem.toString();
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_INFORMACOES_CLIENTE.getCodigo();
    }
}
