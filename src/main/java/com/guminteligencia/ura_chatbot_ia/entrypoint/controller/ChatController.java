package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.ChatUseCase;
import com.guminteligencia.ura_chatbot_ia.domain.Chat;
import com.guminteligencia.ura_chatbot_ia.domain.ConversaAgente;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ChatDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.ChatMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatUseCase chatUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<String>> criar(@RequestBody ConversaAgente conversaAgente) {
        String resultado = chatUseCase.criar(conversaAgente.getId());
        ResponseDto<String> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto<ChatDto>> acessar(@PathVariable("id") UUID idChat) {
        ChatDto resultado = ChatMapper.paraDto(chatUseCase.acessar(idChat));
        ResponseDto<ChatDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }
}
