package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.TorreEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataTorreRepository extends JpaRepository<TorreEntity, Long> {

    Optional<TorreEntity> findByNomeIgnoreCase(String nome);
}
