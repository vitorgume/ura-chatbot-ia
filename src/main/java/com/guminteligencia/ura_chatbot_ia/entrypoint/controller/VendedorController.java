package com.guminteligencia.ura_chatbot_ia.entrypoint.controller;

import com.guminteligencia.ura_chatbot_ia.application.usecase.vendedor.VendedorUseCase;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.ResponseDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.dto.VendedorDto;
import com.guminteligencia.ura_chatbot_ia.entrypoint.mapper.VendedorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("vendedores")
@RequiredArgsConstructor
public class VendedorController {

    private final VendedorUseCase vendedorUseCase;

    @PostMapping
    public ResponseEntity<ResponseDto<VendedorDto>> cadastrar(@RequestBody VendedorDto novoVendedor) {
        VendedorDto resultado = VendedorMapper.paraDto(vendedorUseCase.cadastrar(VendedorMapper.paraDomain(novoVendedor)));
        ResponseDto<VendedorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.created(
                        UriComponentsBuilder
                                .newInstance()
                                .path("/vendedores/{id}")
                                .buildAndExpand(resultado.getId())
                                .toUri())
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto<VendedorDto>> alterar(@RequestBody VendedorDto novosDados, @PathVariable("id") Long idVendedor) {
        VendedorDto resultado = VendedorMapper.paraDto(vendedorUseCase.alterar(VendedorMapper.paraDomain(novosDados), idVendedor));
        ResponseDto<VendedorDto> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<List<VendedorDto>>> listar() {
        List<VendedorDto> resultado = vendedorUseCase.listar().stream().map(VendedorMapper::paraDto).toList();
        ResponseDto<List<VendedorDto>> response = new ResponseDto<>(resultado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable("id") Long idVendedor) {
        vendedorUseCase.deletar(idVendedor);
        return ResponseEntity.noContent().build();
    }

}
