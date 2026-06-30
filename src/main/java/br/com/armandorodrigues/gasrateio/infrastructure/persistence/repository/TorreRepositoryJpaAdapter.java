package br.com.armandorodrigues.gasrateio.infrastructure.persistence.repository;

import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import br.com.armandorodrigues.gasrateio.infrastructure.persistence.mapper.TorreMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TorreRepositoryJpaAdapter implements TorreRepository {

    private final SpringDataTorreRepository springDataTorreRepository;

    public TorreRepositoryJpaAdapter(SpringDataTorreRepository springDataTorreRepository) {
        this.springDataTorreRepository = springDataTorreRepository;
    }

    @Override
    public Torre salvar(Torre torre) {
        return TorreMapper.toDomain(
                springDataTorreRepository.save(TorreMapper.toEntity(torre))
        );
    }

    @Override
    public List<Torre> listarTodas() {
        return springDataTorreRepository.findAllByOrderByNomeAsc()
                .stream()
                .map(TorreMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Torre> buscarPorId(Long id) {
        return springDataTorreRepository.findById(id)
                .map(TorreMapper::toDomain);
    }

    @Override
    public Optional<Torre> buscarPorNome(String nome) {
        return springDataTorreRepository.findByNomeIgnoreCase(nome)
                .map(TorreMapper::toDomain);
    }
}