package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ChatUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ChatDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatUseCase chatUseCase;

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ChatDto>> acessar(@PathVariable("id") UUID idChat) {
        ChatDto resultado = ChatMapper.paraDto(chatUseCase.acessar(idChat));
        ResponseDto<ChatDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
