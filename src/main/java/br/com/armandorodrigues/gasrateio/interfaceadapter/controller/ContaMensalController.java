package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalRequest;
import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarContasMensaisUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.RegistrarContaMensalUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contas-mensais")
public class ContaMensalController {

    private final RegistrarContaMensalUseCase registrarContaMensalUseCase;
    private final ListarContasMensaisUseCase listarContasMensaisUseCase;

    public ContaMensalController(
            RegistrarContaMensalUseCase registrarContaMensalUseCase,
            ListarContasMensaisUseCase listarContasMensaisUseCase
    ) {
        this.registrarContaMensalUseCase = registrarContaMensalUseCase;
        this.listarContasMensaisUseCase = listarContasMensaisUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaMensalResponse registrar(@Valid @RequestBody ContaMensalRequest request) {
        return registrarContaMensalUseCase.executar(request);
    }

    @GetMapping
    public List<ContaMensalResponse> listar() {
        return listarContasMensaisUseCase.executar();
    }
}