package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.RateioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataRateioRepository extends JpaRepository<RateioEntity, Long> {

    Optional<RateioEntity> findByMesReferencia(String mesReferencia);

    List<RateioEntity> findAllByOrderByMesReferenciaDesc();
}