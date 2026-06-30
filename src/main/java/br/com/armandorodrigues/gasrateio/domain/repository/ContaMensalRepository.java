package br.com.armandorodrigues.gasrateio.domain.repository;

import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface ContaMensalRepository {

    ContaMensal salvar(ContaMensal contaMensal);

    List<ContaMensal> listarTodas();

    Optional<ContaMensal> buscarPorMesReferencia(YearMonth mesReferencia);
}