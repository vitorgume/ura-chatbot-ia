package com.guminteligencia.ura_chatbot_ia.application.usecase.mensagem;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.EscolhaNaoIdentificadoException;
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
