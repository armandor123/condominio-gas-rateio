package br.com.armandorodrigues.gasrateio.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.YearMonth;

public record CalcularRateioRequest(

        @NotNull(message = "O mês de referência é obrigatório.")
        YearMonth mesReferencia

) {
}