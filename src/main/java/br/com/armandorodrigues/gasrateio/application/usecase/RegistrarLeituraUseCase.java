package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.LeituraRequest;
import br.com.armandorodrigues.gasrateio.application.dto.LeituraResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Leitura;
import br.com.armandorodrigues.gasrateio.domain.repository.LeituraRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrarLeituraUseCase {

    private final LeituraRepository leituraRepository;
    private final MedidorRepository medidorRepository;

    public RegistrarLeituraUseCase(
            LeituraRepository leituraRepository,
            MedidorRepository medidorRepository
    ) {
        this.leituraRepository = leituraRepository;
        this.medidorRepository = medidorRepository;
    }

    public LeituraResponse executar(LeituraRequest request) {
        validarMedidorExistente(request.medidorId());
        validarLeituraDuplicada(request);

        Leitura leitura = Leitura.nova(
                request.medidorId(),
                request.mesReferencia(),
                request.dataLeitura(),
                request.leituraAnterior(),
                request.leituraAtual()
        );

        Leitura leituraSalva = leituraRepository.salvar(leitura);

        return toResponse(leituraSalva);
    }

    private void validarMedidorExistente(Long medidorId) {
        medidorRepository.buscarPorId(medidorId)
                .orElseThrow(() -> new RegraNegocioException("Medidor não encontrado para o ID informado."));
    }

    private void validarLeituraDuplicada(LeituraRequest request) {
        leituraRepository.buscarPorMedidorEMes(request.medidorId(), request.mesReferencia())
                .ifPresent(leitura -> {
                    throw new RegraNegocioException("Já existe uma leitura cadastrada para este medidor neste mês.");
                });
    }

    private LeituraResponse toResponse(Leitura leitura) {
        return new LeituraResponse(
                leitura.getId(),
                leitura.getMedidorId(),
                leitura.getMesReferencia(),
                leitura.getDataLeitura(),
                leitura.getLeituraAnterior(),
                leitura.getLeituraAtual(),
                leitura.getConsumo()
        );
    }
}