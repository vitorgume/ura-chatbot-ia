package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.LoginUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.LoginResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<LoginResponseDto>> logar(@RequestBody LoginDto loginDto) {
        LoginResponseDto resultado = LoginMapper.paraDto(loginUseCase.autenticar(loginDto.getEmail(), loginDto.getSenha()));
        ResponseDto<LoginResponseDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(UriComponentsBuilder
                .newInstance()
                .build()
                .toUri()
        ).body(response);
    }
}
