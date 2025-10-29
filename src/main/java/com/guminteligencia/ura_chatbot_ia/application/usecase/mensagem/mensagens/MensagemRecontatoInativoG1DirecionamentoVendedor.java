package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRecontatoInativoG1DirecionamentoVendedor implements MensagemType {
    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Perfeito, estou te redirecionando para o vendedor(a) " + nomeVendedor + " que logo entrará em contato. Muito obrigado ! Até...";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR.getCodigo();
    }
}
