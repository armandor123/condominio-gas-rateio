package br.com.armandorodrigues.gasrateio.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record ContaMensalRequest(

        @NotNull(message = "O mês de referência é obrigatório.")
        YearMonth mesReferencia,

        @NotNull(message = "O valor total da conta é obrigatório.")
        @DecimalMin(value = "0.01", message = "O valor total da conta deve ser maior que zero.")
        BigDecimal valorTotal,

        @NotNull(message = "O consumo informado é obrigatório.")
        @DecimalMin(value = "0.00", message = "O consumo informado não pode ser negativo.")
        BigDecimal consumoInformado,

        @NotNull(message = "A data de vencimento é obrigatória.")
        LocalDate dataVencimento,

        @Size(max = 50, message = "O número da fatura deve ter no máximo 50 caracteres.")
        String numeroFatura,

        @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
        String observacoes

) {
}