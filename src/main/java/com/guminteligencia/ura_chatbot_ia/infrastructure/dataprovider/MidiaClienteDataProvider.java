package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.MidiaClienteGateway;
import com.guminteligencia.ura_chatbot_ia.domain.MidiaCliente;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.MidiaClienteMapper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.MidiaCLienteRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.MidiaCLienteEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MidiaClienteDataProvider implements MidiaClienteGateway {

    private final MidiaCLienteRepository repository;
    private final String MENSAGEM_ERRO_CONSULTAR_MIDIA_CLIENTE_PELO_TELEFONE_CLIENTE = "Erro ao consultar midia de cliente pelo seu telefone.";

    @Override
    public Optional<MidiaCliente> consultarMidiaPeloTelefoneCliente(String telefone) {
        Optional<MidiaCLienteEntity> midiaCLienteEntity;

        try {
            midiaCLienteEntity = repository.findByTelefoneClienteFetch(telefone);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_MIDIA_CLIENTE_PELO_TELEFONE_CLIENTE, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_MIDIA_CLIENTE_PELO_TELEFONE_CLIENTE, ex.getCause());
        }

        return midiaCLienteEntity.map(MidiaClienteMapper::paraDomain);
    }
}
