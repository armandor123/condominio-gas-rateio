package br.com.armandorodrigues.gasrateio.domain.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Rateio;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface RateioRepository {

    Rateio salvar(Rateio rateio);

    List<Rateio> listarTodos();

    Optional<Rateio> buscarPorMesReferencia(YearMonth mesReferencia);
}