package br.com.armandorodrigues.gasrateio.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TorreRequest(

        @NotBlank(message = "O nome da torre é obrigatório.")
        @Size(min = 2, max = 100, message = "O nome da torre deve ter entre 2 e 100 caracteres.")
        String nome

) {
}

