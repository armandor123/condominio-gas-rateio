package br.com.armandorodrigues.gasrateio.application.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public record RateioResponse(
        Long id,
        YearMonth mesReferencia,
        Long contaMensalId,
        BigDecimal valorTotalConta,
        BigDecimal consumoTotalSecundario,
        BigDecimal consumoMedidorPrincipal,
        BigDecimal diferencaConsumo,
        LocalDateTime dataCalculo,
        List<ItemRateioResponse> itens
) {
}