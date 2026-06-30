package br.com.armandorodrigues.gasrateio.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record LeituraRequest(

        @NotNull(message = "O medidor é obrigatório.")
        Long medidorId,

        @NotNull(message = "O mês de referência é obrigatório.")
        YearMonth mesReferencia,

        @NotNull(message = "A data da leitura é obrigatória.")
        LocalDate dataLeitura,

        @NotNull(message = "A leitura anterior é obrigatória.")
        @DecimalMin(value = "0.00", message = "A leitura anterior não pode ser negativa.")
        BigDecimal leituraAnterior,

        @NotNull(message = "A leitura atual é obrigatória.")
        @DecimalMin(value = "0.00", message = "A leitura atual não pode ser negativa.")
        BigDecimal leituraAtual

) {
}