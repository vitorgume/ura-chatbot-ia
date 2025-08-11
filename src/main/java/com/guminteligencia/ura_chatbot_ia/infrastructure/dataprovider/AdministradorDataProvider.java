package com.guminteligencia.ura_chatbot_ia.infrastructure.dataprovider;

import com.guminteligencia.ura_chatbot_ia.application.gateways.AdministradorGateway;
import com.guminteligencia.ura_chatbot_ia.domain.Administrador;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import com.guminteligencia.ura_chatbot_ia.infrastructure.mapper.AdministradorMappper;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.AdministradorRepository;
import com.guminteligencia.ura_chatbot_ia.infrastructure.repository.entity.AdministradorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdministradorDataProvider implements AdministradorGateway {

    private final AdministradorRepository repository;
    private final String MENSAGEM_ERRO_CONSULTAR_POR_EMAIL = "Erro ao consultar administrador pelo seu email.";
    private final String MENSAGEM_ERRO_SALVAR = "Erro ao salvar novo administrador.";
    private final String MENSAGEM_ERRO_DELETAR = "Erro ao deletar administrador.";

    @Override
    public Optional<Administrador> consultarPorTelefone(String email) {
        Optional<AdministradorEntity> administradorEntity;

        try {
            administradorEntity = repository.findByTelefone(email);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex);
            throw new DataProviderException(MENSAGEM_ERRO_CONSULTAR_POR_EMAIL, ex.getCause());
        }

        return administradorEntity.map(AdministradorMappper::paraDomain);
    }

    @Override
    public Administrador salvar(Administrador novoAdministrador) {
        AdministradorEntity administradorEntity = AdministradorMappper.paraEntity(novoAdministrador);

        try {
            administradorEntity = repository.save(administradorEntity);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_SALVAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_SALVAR, ex.getCause());
        }

        return AdministradorMappper.paraDomain(administradorEntity);
    }

    @Override
    public void deletar(UUID id) {
        try {
            repository.deleteById(id);
        } catch (Exception ex) {
            log.error(MENSAGEM_ERRO_DELETAR, ex);
            throw new DataProviderException(MENSAGEM_ERRO_DELETAR, ex.getCause());
        }

    }
}
