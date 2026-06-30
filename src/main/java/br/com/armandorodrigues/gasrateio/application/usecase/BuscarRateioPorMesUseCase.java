package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ItemRateioResponse;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.repository.RateioRepository;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class BuscarRateioPorMesUseCase {

    private final RateioRepository rateioRepository;

    public BuscarRateioPorMesUseCase(RateioRepository rateioRepository) {
        this.rateioRepository = rateioRepository;
    }

    public RateioResponse executar(String mesReferenciaTexto) {
        YearMonth mesReferencia = converterMesReferencia(mesReferenciaTexto);

        Rateio rateio = rateioRepository.buscarPorMesReferencia(mesReferencia)
                .orElseThrow(() -> new RegraNegocioException("Rateio não encontrado para o mês informado."));

        return toResponse(rateio);
    }

    private YearMonth converterMesReferencia(String mesReferenciaTexto) {
        try {
            return YearMonth.parse(mesReferenciaTexto);
        } catch (DateTimeParseException exception) {
            throw new RegraNegocioException("Mês de referência inválido. Use o formato yyyy-MM.");
        }
    }

    private RateioResponse toResponse(Rateio rateio) {
        return new RateioResponse(
                rateio.getId(),
                rateio.getMesReferencia(),
                rateio.getContaMensalId(),
                rateio.getValorTotalConta(),
                rateio.getConsumoTotalSecundario(),
                rateio.getConsumoMedidorPrincipal(),
                rateio.getDiferencaConsumo(),
                rateio.getDataCalculo(),
                toItensResponse(rateio.getItens())
        );
    }

    private List<ItemRateioResponse> toItensResponse(List<ItemRateio> itens) {
        return itens.stream()
                .map(item -> new ItemRateioResponse(
                        item.getId(),
                        item.getTorreId(),
                        item.getNomeTorre(),
                        item.getMedidorId(),
                        item.getConsumo(),
                        item.getPercentual(),
                        item.getValorRateado()
                ))
                .toList();
    }
}