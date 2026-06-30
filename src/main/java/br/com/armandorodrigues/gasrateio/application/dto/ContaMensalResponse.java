package br.com.armandorodrigues.gasrateio.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record ContaMensalResponse(
        Long id,
        YearMonth mesReferencia,
        BigDecimal valorTotal,
        BigDecimal consumoInformado,
        LocalDate dataVencimento,
        String numeroFatura,
        String observacoes
) {
}