package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper.MedidorMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MedidorRepositoryJpaAdapter implements MedidorRepository {

    private final SpringDataMedidorRepository springDataMedidorRepository;

    public MedidorRepositoryJpaAdapter(SpringDataMedidorRepository springDataMedidorRepository) {
        this.springDataMedidorRepository = springDataMedidorRepository;
    }

    @Override
    public Medidor salvar(Medidor medidor) {
        return MedidorMapper.toDomain(
                springDataMedidorRepository.save(MedidorMapper.toEntity(medidor))
        );
    }

    @Override
    public List<Medidor> listarTodos() {
        return springDataMedidorRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(MedidorMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Medidor> buscarPorId(Long id) {
        return springDataMedidorRepository.findById(id)
                .map(MedidorMapper::toDomain);
    }

    @Override
    public Optional<Medidor> buscarPorCodigo(String codigo) {
        return springDataMedidorRepository.findByCodigoIgnoreCase(codigo)
                .map(MedidorMapper::toDomain);
    }
}