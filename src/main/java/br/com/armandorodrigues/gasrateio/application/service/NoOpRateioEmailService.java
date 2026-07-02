package br.com.armandorodrigues.gasrateio.application.service;

import br.com.armandorodrigues.gasrateio.domain.model.Rateio;

public class NoOpRateioEmailService implements RateioEmailService {

    @Override
    public void enviarRateioCalculado(Rateio rateio) {
        // Não faz nada.
        // Usado em testes unitários para não depender de SMTP.
    }
}
