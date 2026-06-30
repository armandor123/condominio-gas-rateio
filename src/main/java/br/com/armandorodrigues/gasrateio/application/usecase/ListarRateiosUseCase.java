package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ItemRateioResponse;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.repository.RateioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarRateiosUseCase {

    private final RateioRepository rateioRepository;

    public ListarRateiosUseCase(RateioRepository rateioRepository) {
        this.rateioRepository = rateioRepository;
    }

    public List<RateioResponse> executar() {
        return rateioRepository.listarTodos()
                .stream()
                .map(this::toResponse)
                .toList();
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