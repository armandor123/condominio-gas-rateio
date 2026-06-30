package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.repository.RateioRepository;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper.RateioMapper;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class RateioRepositoryJpaAdapter implements RateioRepository {

    private final SpringDataRateioRepository springDataRateioRepository;

    public RateioRepositoryJpaAdapter(SpringDataRateioRepository springDataRateioRepository) {
        this.springDataRateioRepository = springDataRateioRepository;
    }

    @Override
    public Rateio salvar(Rateio rateio) {
        return RateioMapper.toDomain(
                springDataRateioRepository.save(RateioMapper.toEntity(rateio))
        );
    }

    @Override
    public List<Rateio> listarTodos() {
        return springDataRateioRepository.findAllByOrderByMesReferenciaDesc()
                .stream()
                .map(RateioMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Rateio> buscarPorMesReferencia(YearMonth mesReferencia) {
        return springDataRateioRepository.findByMesReferencia(mesReferencia.toString())
                .map(RateioMapper::toDomain);
    }
}