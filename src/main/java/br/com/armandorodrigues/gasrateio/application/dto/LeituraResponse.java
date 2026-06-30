package br.com.armandorodrigues.gasrateio.application.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public record LeituraResponse(
        Long id,
        Long medidorId,
        YearMonth mesReferencia,
        LocalDate dataLeitura,
        BigDecimal leituraAnterior,
        BigDecimal leituraAtual,
        BigDecimal consumo
) {
}