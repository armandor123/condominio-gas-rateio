package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarTorresUseCase {

    private final TorreRepository torreRepository;

    public ListarTorresUseCase(TorreRepository torreRepository) {
        this.torreRepository = torreRepository;
    }

    public List<TorreResponse> executar() {
        return torreRepository.listarTodas()
                .stream()
                .map(torre -> new TorreResponse(
                        torre.getId(),
                        torre.getNome(),
                        torre.isAtiva()
                ))
                .toList();
    }
}
