package com.guminteligencia.ura_chatbot_ia.entrypoint.controller.middleware;

import com.guminteligencia.ura_chatbot_ia.application.exceptions.*;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.infrastructure.exceptions.DataProviderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class HandlerMiddleware {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto> exceptionHandler(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    //Administrador

    @ExceptionHandler(AdministradorJaExisteException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerAdministradorJaExisteException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(AdministradorNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerAdministradorNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    //Cliente

    @ExceptionHandler(ClienteNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerClienteNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(CredenciasIncorretasException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerCredenciasIncorretasException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    //Contexto

    @ExceptionHandler(ContextoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerContextoNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    //Conversa

    @ExceptionHandler(ConversaAgenteNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerConversaAgenteNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(EscolhaNaoIdentificadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerEscolhaNaoIdentificadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(OutroContatoNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerOutroContatoNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(ProcessoContextoExistenteNaoIdentificadoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerProcessoContextoExistenteNaoIdentificadoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(VendedorComMesmoTelefoneException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorComMesmoTelefoneException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(VendedorNaoEncontradoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorNaoEncontradoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(VendedorNaoEscolhidoException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerVendedorNaoEscolhidoException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    @ExceptionHandler(DataProviderException.class)
    public ResponseEntity<ResponseDto> exceptionHandlerDataProviderException(Exception exception) {
        ResponseDto.ErroDto erroDto = ResponseDto.ErroDto.builder()
                .mensagens(mensagens(exception.getMessage()))
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ResponseDto.comErro(erroDto));
    }

    private List<String> mensagens(String mensagem) {
        return mensagem != null ? List.of(mensagem) : List.of("Erro interno inesperado.");
    }
}
