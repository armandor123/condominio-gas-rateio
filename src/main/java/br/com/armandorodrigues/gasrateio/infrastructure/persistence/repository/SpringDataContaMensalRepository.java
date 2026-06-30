package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.ContaMensalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataContaMensalRepository extends JpaRepository<ContaMensalEntity, Long> {

    Optional<ContaMensalEntity> findByMesReferencia(String mesReferencia);

    List<ContaMensalEntity> findAllByOrderByMesReferenciaDesc();
}