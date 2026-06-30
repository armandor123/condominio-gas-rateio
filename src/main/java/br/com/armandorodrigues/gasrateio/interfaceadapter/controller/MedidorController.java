package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorRequest;
import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.CadastrarMedidorUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarMedidoresUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/medidores")
public class MedidorController {

    private final CadastrarMedidorUseCase cadastrarMedidorUseCase;
    private final ListarMedidoresUseCase listarMedidoresUseCase;

    public MedidorController(
            CadastrarMedidorUseCase cadastrarMedidorUseCase,
            ListarMedidoresUseCase listarMedidoresUseCase
    ) {
        this.cadastrarMedidorUseCase = cadastrarMedidorUseCase;
        this.listarMedidoresUseCase = listarMedidoresUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedidorResponse cadastrar(@Valid @RequestBody MedidorRequest request) {
        return cadastrarMedidorUseCase.executar(request);
    }

    @GetMapping
    public List<MedidorResponse> listar() {
        return listarMedidoresUseCase.executar();
    }
}