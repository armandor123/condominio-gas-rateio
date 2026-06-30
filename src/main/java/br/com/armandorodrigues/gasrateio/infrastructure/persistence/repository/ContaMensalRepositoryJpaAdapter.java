package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.repository.ContaMensalRepository;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper.ContaMensalMapper;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class ContaMensalRepositoryJpaAdapter implements ContaMensalRepository {

    private final SpringDataContaMensalRepository springDataContaMensalRepository;

    public ContaMensalRepositoryJpaAdapter(SpringDataContaMensalRepository springDataContaMensalRepository) {
        this.springDataContaMensalRepository = springDataContaMensalRepository;
    }

    @Override
    public ContaMensal salvar(ContaMensal contaMensal) {
        return ContaMensalMapper.toDomain(
                springDataContaMensalRepository.save(ContaMensalMapper.toEntity(contaMensal))
        );
    }

    @Override
    public List<ContaMensal> listarTodas() {
        return springDataContaMensalRepository.findAllByOrderByMesReferenciaDesc()
                .stream()
                .map(ContaMensalMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<ContaMensal> buscarPorMesReferencia(YearMonth mesReferencia) {
        return springDataContaMensalRepository.findByMesReferencia(mesReferencia.toString())
                .map(ContaMensalMapper::toDomain);
    }
}