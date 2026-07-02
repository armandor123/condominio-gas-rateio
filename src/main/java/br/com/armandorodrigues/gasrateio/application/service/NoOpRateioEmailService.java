package br.com.armandorodrigues.gasrateio.application.service;

import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(
        name = "app.email.enabled",
        havingValue = "false",
        matchIfMissing = true
)
public class NoOpRateioEmailService implements RateioEmailService {

    @Override
    public void enviarRateioCalculado(Rateio rateio) {
        // Não faz nada.
        // Usado quando o envio de e-mail está desativado.
    }
}
