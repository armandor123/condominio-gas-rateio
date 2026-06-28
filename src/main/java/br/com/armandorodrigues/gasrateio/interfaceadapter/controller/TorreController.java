package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.TorreRequest;
import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.CadastrarTorreUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarTorresUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/torres")
public class TorreController {

    private final CadastrarTorreUseCase cadastrarTorreUseCase;
    private final ListarTorresUseCase listarTorresUseCase;

    public TorreController(
            CadastrarTorreUseCase cadastrarTorreUseCase,
            ListarTorresUseCase listarTorresUseCase
    ) {
        this.cadastrarTorreUseCase = cadastrarTorreUseCase;
        this.listarTorresUseCase = listarTorresUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TorreResponse cadastrar(@Valid @RequestBody TorreRequest request) {
        return cadastrarTorreUseCase.executar(request);
    }

    @GetMapping
    public List<TorreResponse> listar() {
        return listarTorresUseCase.executar();
    }
}
