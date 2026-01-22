package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemDirecionamentoVendedor implements MensagemType {
    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Muito obrigado pelas informações ! Agora você será redirecionado para a " + nomeVendedor + ". Você consegue chama-la nesse número ?";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.MENSAGEM_DIRECIONAMENTO_VENDEDOR.getCodigo();
    }
}
