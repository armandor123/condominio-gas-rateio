package br.com.armandorodrigues.gasrateio.application.dto;

import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;

public record MedidorResponse(
        Long id,
        String nome,
        String codigo,
        TipoMedidor tipo,
        Long torreId,
        boolean ativo
) {
}