package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Leitura;
import br.com.armandorodrigues.gasrateio.domain.repository.LeituraRepository;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper.LeituraMapper;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class LeituraRepositoryJpaAdapter implements LeituraRepository {

    private final SpringDataLeituraRepository springDataLeituraRepository;

    public LeituraRepositoryJpaAdapter(SpringDataLeituraRepository springDataLeituraRepository) {
        this.springDataLeituraRepository = springDataLeituraRepository;
    }

    @Override
    public Leitura salvar(Leitura leitura) {
        return LeituraMapper.toDomain(
                springDataLeituraRepository.save(LeituraMapper.toEntity(leitura))
        );
    }

    @Override
    public List<Leitura> listarTodas() {
        return springDataLeituraRepository.findAllByOrderByMesReferenciaDescDataLeituraDesc()
                .stream()
                .map(LeituraMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Leitura> buscarPorMedidorEMes(Long medidorId, YearMonth mesReferencia) {
        return springDataLeituraRepository
                .findByMedidorIdAndMesReferencia(medidorId, mesReferencia.toString())
                .map(LeituraMapper::toDomain);
    }
}