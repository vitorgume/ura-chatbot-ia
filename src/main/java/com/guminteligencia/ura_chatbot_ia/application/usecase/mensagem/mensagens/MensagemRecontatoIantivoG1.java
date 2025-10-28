package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import com.guminteligencia.ura_chatbot_ia.domain.Cliente;
import org.springframework.stereotype.Component;

@Component
public class MensagemRecontatoIantivoG1 implements MensagemType {

    @Override
    public String getMensagem(String nomeVendedor, Cliente cliente) {
        return "Oii! Aqui é da Gráfica Neoprint! Você nos enviou mensagem, mas não deu continuidade no atendimento! Se você quiser que eu te conecte com um vendedor, é só me responder essa mensagem, ok?";
    }

    @Override
    public Integer getTipoMensagem() {
        return TipoMensagem.RECONTATO_INATIVO_G1.getCodigo();
    }
}
