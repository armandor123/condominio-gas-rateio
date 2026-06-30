package br.com.armandorodrigues.gasrateio.domain.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Torre;

import java.util.List;
import java.util.Optional;

public interface TorreRepository {

    Torre salvar(Torre torre);

    List<Torre> listarTodas();

    Optional<Torre> buscarPorId(Long id);

    Optional<Torre> buscarPorNome(String nome);
}
