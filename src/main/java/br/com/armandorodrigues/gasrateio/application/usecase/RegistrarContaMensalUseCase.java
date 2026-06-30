package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalRequest;
import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.repository.ContaMensalRepository;
import org.springframework.stereotype.Service;

@Service
public class RegistrarContaMensalUseCase {

    private final ContaMensalRepository contaMensalRepository;

    public RegistrarContaMensalUseCase(ContaMensalRepository contaMensalRepository) {
        this.contaMensalRepository = contaMensalRepository;
    }

    public ContaMensalResponse executar(ContaMensalRequest request) {
        validarContaDuplicada(request);

        ContaMensal contaMensal = ContaMensal.nova(
                request.mesReferencia(),
                request.valorTotal(),
                request.consumoInformado(),
                request.dataVencimento(),
                request.numeroFatura(),
                request.observacoes()
        );

        ContaMensal contaSalva = contaMensalRepository.salvar(contaMensal);

        return toResponse(contaSalva);
    }

    private void validarContaDuplicada(ContaMensalRequest request) {
        contaMensalRepository.buscarPorMesReferencia(request.mesReferencia())
                .ifPresent(conta -> {
                    throw new RegraNegocioException("Já existe uma conta mensal cadastrada para este mês.");
                });
    }

    private ContaMensalResponse toResponse(ContaMensal contaMensal) {
        return new ContaMensalResponse(
                contaMensal.getId(),
                contaMensal.getMesReferencia(),
                contaMensal.getValorTotal(),
                contaMensal.getConsumoInformado(),
                contaMensal.getDataVencimento(),
                contaMensal.getNumeroFatura(),
                contaMensal.getObservacoes()
        );
    }
}