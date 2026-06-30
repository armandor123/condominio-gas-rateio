package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.LeituraRequest;
import br.com.armandorodrigues.gasrateio.application.dto.LeituraResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarLeiturasUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.RegistrarLeituraUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/leituras")
public class LeituraController {

    private final RegistrarLeituraUseCase registrarLeituraUseCase;
    private final ListarLeiturasUseCase listarLeiturasUseCase;

    public LeituraController(
            RegistrarLeituraUseCase registrarLeituraUseCase,
            ListarLeiturasUseCase listarLeiturasUseCase
    ) {
        this.registrarLeituraUseCase = registrarLeituraUseCase;
        this.listarLeiturasUseCase = listarLeiturasUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeituraResponse registrar(@Valid @RequestBody LeituraRequest request) {
        return registrarLeituraUseCase.executar(request);
    }

    @GetMapping
    public List<LeituraResponse> listar() {
        return listarLeiturasUseCase.executar();
    }
}