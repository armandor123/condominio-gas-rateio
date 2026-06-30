package br.com.armandorodrigues.gasrateio.application.dto;

import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedidorRequest(

        @NotBlank(message = "O nome do medidor é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome do medidor deve ter entre 2 e 100 caracteres.")
        String nome,

        @NotBlank(message = "O código do medidor é obrigatório.")
        @Size(max = 50, message = "O código do medidor deve ter no máximo 50 caracteres.")
        String codigo,

        @NotNull(message = "O tipo do medidor é obrigatório.")
        TipoMedidor tipo,

        Long torreId

) {
}