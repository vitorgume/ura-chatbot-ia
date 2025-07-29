package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.AdministradorUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.AdministradorDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.AdministradorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.UUID;

@RestController
@RequestMapping("administradores")
@RequiredArgsConstructor
public class AdministradorController {

    private final AdministradorUseCase administradorUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<AdministradorDto>> cadastrar(@RequestBody AdministradorDto novoAdministrador) {
        AdministradorDto resultado = AdministradorMapper.paraDto(administradorUseCase.cadastrar(AdministradorMapper.paraDomain(novoAdministrador)));
        ResponseDto<AdministradorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .path("/administradores/{id}")
                .buildAndExpand(resultado.getId())
                .toUri()
        ).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") UUID id) {
        administradorUseCase.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
