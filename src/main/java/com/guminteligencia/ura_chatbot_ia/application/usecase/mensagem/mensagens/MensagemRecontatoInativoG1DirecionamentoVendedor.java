package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRecontatoInativoG1DirecionamentoVendedor implements MensagemType {
    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Perfeito, estou te redirecionando para a vendedora " + nomeVendedor + " que logo entrará em contato. Você consegue chama-la nesse número ?";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.RECONTATO_INATIVO_G1_DIRECIONAMENTO_VENDEDOR.getCodigo();
    }
}
