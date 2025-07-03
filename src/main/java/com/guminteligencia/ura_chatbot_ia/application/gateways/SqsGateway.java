package com.guminteligencia.ura_chatbot_ia.application.gateways;

import com.guminteligencia.ura_chatbot_ia.domain.Contexto;

import java.util.List;

public interface SqsGateway {
    List<Contexto> listarContextos();
}
