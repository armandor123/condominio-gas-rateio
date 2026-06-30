package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity.LeituraEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataLeituraRepository extends JpaRepository<LeituraEntity, Long> {

    Optional<LeituraEntity> findByMedidorIdAndMesReferencia(Long medidorId, String mesReferencia);

    List<LeituraEntity> findAllByOrderByMesReferenciaDescDataLeituraDesc();
}