package br.com.armandorodrigues.gasrateio.application.service;

import br.com.armandorodrigues.gasrateio.domain.model.Rateio;

public interface RateioEmailService {

    void enviarRateioCalculado(Rateio rateio);
}
