package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.mensagens;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
import com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem.TipoMensagem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MensagemFactory {

    private final List<MensagemType> mensagens;

    public MensagemType create(TipoMensagem tipo) {
        return mensagens.stream()
                .filter(mensagem -> mensagem.getTipoMensagem().equals(tipo.getCodigo()))
                .findFirst()
                .orElseThrow(EscolhaNaoIdentificadoException::new);
    }
}
