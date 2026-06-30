package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.MedidorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataMedidorRepository extends JpaRepository<MedidorEntity, Long> {

    Optional<MedidorEntity> findByCodigoIgnoreCase(String codigo);

    List<MedidorEntity> findAllByOrderByNomeAsc();
}