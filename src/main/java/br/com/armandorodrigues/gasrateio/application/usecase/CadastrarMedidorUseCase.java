package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorRequest;
import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import org.springframework.stereotype.Service;

@Service
public class CadastrarMedidorUseCase {

    private final MedidorRepository medidorRepository;
    private final TorreRepository torreRepository;

    public CadastrarMedidorUseCase(
            MedidorRepository medidorRepository,
            TorreRepository torreRepository
    ) {
        this.medidorRepository = medidorRepository;
        this.torreRepository = torreRepository;
    }

    public MedidorResponse executar(MedidorRequest request) {
        medidorRepository.buscarPorCodigo(request.codigo())
                .ifPresent(medidor -> {
                    throw new RegraNegocioException("Já existe um medidor cadastrado com este código.");
                });

        Medidor medidor = Medidor.novo(
                request.nome(),
                request.codigo(),
                request.tipo(),
                request.torreId()
        );

        if (medidor.getTipo() == TipoMedidor.SECUNDARIO) {
            torreRepository.buscarPorId(medidor.getTorreId())
                    .orElseThrow(() -> new RegraNegocioException("Torre não encontrada para o ID informado."));
        }

        Medidor medidorSalvo = medidorRepository.salvar(medidor);

        return new MedidorResponse(
                medidorSalvo.getId(),
                medidorSalvo.getNome(),
                medidorSalvo.getCodigo(),
                medidorSalvo.getTipo(),
                medidorSalvo.getTorreId(),
                medidorSalvo.isAtivo()
        );
    }
}
