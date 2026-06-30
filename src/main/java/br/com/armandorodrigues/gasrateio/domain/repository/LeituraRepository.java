package br.com.armandorodrigues.gasrateio.domain.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Leitura;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface LeituraRepository {

    Leitura salvar(Leitura leitura);

    List<Leitura> listarTodas();

    Optional<Leitura> buscarPorMedidorEMes(Long medidorId, YearMonth mesReferencia);
}
