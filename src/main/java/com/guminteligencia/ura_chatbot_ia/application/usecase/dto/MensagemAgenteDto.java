package com.guminteligencia.ura_chatbot_ia.application.usecase.dto;

import com.guminteligencia.ura_chatbot_ia.domain.MensagemContexto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class MensagemAgenteDto {
    private String clienteId;
    private String conversaId;
    private String  mensagem;
    private List<String> audiosUrl;
    private List<String> imagensUrl;
}
