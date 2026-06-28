package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.TorreRequest;
import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarTorreUseCase {

    private final TorreRepository torreRepository;

    public CadastrarTorreUseCase(TorreRepository torreRepository) {
        this.torreRepository = torreRepository;
    }

    public TorreResponse executar(TorreRequest request) {
        torreRepository.buscarPorNome(request.nome())
                .ifPresent(torre -> {
                    throw new RegraNegocioException("Já existe uma torre cadastrada com este nome.");
                });

        Torre torre = Torre.nova(request.nome());

        Torre torreSalva = torreRepository.salvar(torre);

        return new TorreResponse(
                torreSalva.getId(),
                torreSalva.getNome(),
                torreSalva.isAtiva()
        );
    }
}
