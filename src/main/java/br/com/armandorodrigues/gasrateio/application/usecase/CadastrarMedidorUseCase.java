package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import br.com.armandorodrigues.gasrateio.application.dto.MedidorRequest;
import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
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
        validarCodigoUnico(request.codigo());

        Medidor medidor = criarMedidor(request);

        Medidor medidorSalvo = medidorRepository.salvar(medidor);

        return toResponse(medidorSalvo);
    }

    private void validarCodigoUnico(String codigo) {
        medidorRepository.buscarPorCodigo(codigo)
                .ifPresent(medidor -> {
                    throw new RegraNegocioException("Já existe um medidor cadastrado com este código.");
                });
    }

    private Medidor criarMedidor(MedidorRequest request) {
        if (request.tipo() == TipoMedidor.PRINCIPAL) {
            return Medidor.novoPrincipal(
                    request.nome(),
                    request.codigo()
            );
        }

        if (request.tipo() == TipoMedidor.SECUNDARIO) {
            validarTorreExistente(request.torreId());

            return Medidor.novoSecundario(
                    request.nome(),
                    request.codigo(),
                    request.torreId()
            );
        }

        throw new RegraNegocioException("Tipo de medidor inválido.");
    }

    private void validarTorreExistente(Long torreId) {
        if (torreId == null) {
            throw new RegraNegocioException("Medidor secundário deve estar vinculado a uma torre.");
        }

        torreRepository.buscarPorId(torreId)
                .orElseThrow(() -> new RegraNegocioException("Torre não encontrada para o ID informado."));
    }

    private MedidorResponse toResponse(Medidor medidor) {
        return new MedidorResponse(
                medidor.getId(),
                medidor.getNome(),
                medidor.getCodigo(),
                medidor.getTipo(),
                medidor.getTorreId(),
                medidor.isAtivo()
        );
    }
}