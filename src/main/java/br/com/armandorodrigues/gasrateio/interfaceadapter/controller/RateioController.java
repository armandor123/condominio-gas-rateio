package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.BuscarRateioPorMesUseCase;
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
    private final BuscarRateioPorMesUseCase buscarRateioPorMesUseCase;

    public RateioController(
            CalcularRateioUseCase calcularRateioUseCase,
            ListarRateiosUseCase listarRateiosUseCase,
            BuscarRateioPorMesUseCase buscarRateioPorMesUseCase
    ) {
        this.calcularRateioUseCase = calcularRateioUseCase;
        this.listarRateiosUseCase = listarRateiosUseCase;
        this.buscarRateioPorMesUseCase = buscarRateioPorMesUseCase;
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

    @GetMapping("/{mesReferencia}")
    public RateioResponse buscarPorMes(@PathVariable String mesReferencia) {
        return buscarRateioPorMesUseCase.executar(mesReferencia);
    }
}