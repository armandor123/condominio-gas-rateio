package br.com.armandorodrigues.gasrateio.domain.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Medidor;

import java.util.List;
import java.util.Optional;

public interface MedidorRepository {

    Medidor salvar(Medidor medidor);

    List<Medidor> listarTodos();

    Optional<Medidor> buscarPorId(Long id);

    Optional<Medidor> buscarPorCodigo(String codigo);
}