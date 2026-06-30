package br.com.armandorodrigues.gasrateio.application.dto;

import java.math.BigDecimal;

public record ItemRateioResponse(
        Long id,
        Long torreId,
        String nomeTorre,
        Long medidorId,
        BigDecimal consumo,
        BigDecimal percentual,
        BigDecimal valorRateado
) {
}