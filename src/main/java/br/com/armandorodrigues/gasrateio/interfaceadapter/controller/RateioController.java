package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.CalcularRateioUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarRateiosUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rateios")
public class RateioController {

    private final CalcularRateioUseCase calcularRateioUseCase;
    private final ListarRateiosUseCase listarRateiosUseCase;

    public RateioController(
            CalcularRateioUseCase calcularRateioUseCase,
            ListarRateiosUseCase listarRateiosUseCase
    ) {
        this.calcularRateioUseCase = calcularRateioUseCase;
        this.listarRateiosUseCase = listarRateiosUseCase;
    }

    @PostMapping("/calcular")
    @ResponseStatus(HttpStatus.CREATED)
    public RateioResponse calcular(@Valid @RequestBody CalcularRateioRequest request) {
        return calcularRateioUseCase.executar(request);
    }

    @GetMapping
    public List<RateioResponse> listar() {
        return listarRateiosUseCase.executar();
    }
}