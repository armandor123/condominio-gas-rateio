package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.dto.ItemRateioResponse;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.*;
import br.com.armandorodrigues.gasrateio.domain.repository.*;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio.DadosConsumoTorre;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CalcularRateioUseCase {

    private final ContaMensalRepository contaMensalRepository;
    private final LeituraRepository leituraRepository;
    private final MedidorRepository medidorRepository;
    private final TorreRepository torreRepository;
    private final RateioRepository rateioRepository;
    private final CalculadoraRateio calculadoraRateio;

    public CalcularRateioUseCase(
            ContaMensalRepository contaMensalRepository,
            LeituraRepository leituraRepository,
            MedidorRepository medidorRepository,
            TorreRepository torreRepository,
            RateioRepository rateioRepository
    ) {
        this.contaMensalRepository = contaMensalRepository;
        this.leituraRepository = leituraRepository;
        this.medidorRepository = medidorRepository;
        this.torreRepository = torreRepository;
        this.rateioRepository = rateioRepository;
        this.calculadoraRateio = new CalculadoraRateio();
    }

    public RateioResponse executar(CalcularRateioRequest request) {
        validarRateioDuplicado(request);

        ContaMensal contaMensal = buscarContaMensal(request);

        List<Medidor> medidores = medidorRepository.listarTodos();

        Medidor medidorPrincipal = buscarMedidorPrincipal(medidores);

        Leitura leituraPrincipal = buscarLeituraDoMedidorPrincipal(
                medidorPrincipal,
                request
        );

        List<DadosConsumoTorre> consumosTorres = montarConsumosDasTorres(
                medidores,
                request
        );

        Rateio rateioCalculado = calculadoraRateio.calcular(
                contaMensal,
                leituraPrincipal.getConsumo(),
                consumosTorres
        );

        Rateio rateioSalvo = rateioRepository.salvar(rateioCalculado);

        return toResponse(rateioSalvo);
    }

    private void validarRateioDuplicado(CalcularRateioRequest request) {
        rateioRepository.buscarPorMesReferencia(request.mesReferencia())
                .ifPresent(rateio -> {
                    throw new RegraNegocioException("Já existe um rateio calculado para este mês.");
                });
    }

    private ContaMensal buscarContaMensal(CalcularRateioRequest request) {
        return contaMensalRepository.buscarPorMesReferencia(request.mesReferencia())
                .orElseThrow(() -> new RegraNegocioException("Conta mensal não encontrada para o mês informado."));
    }

    private Medidor buscarMedidorPrincipal(List<Medidor> medidores) {
        return medidores.stream()
                .filter(medidor -> medidor.getTipo() == TipoMedidor.PRINCIPAL)
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("Medidor principal não encontrado."));
    }

    private Leitura buscarLeituraDoMedidorPrincipal(
            Medidor medidorPrincipal,
            CalcularRateioRequest request
    ) {
        return leituraRepository
                .buscarPorMedidorEMes(medidorPrincipal.getId(), request.mesReferencia())
                .orElseThrow(() -> new RegraNegocioException("Leitura do medidor principal não encontrada para o mês informado."));
    }

    private List<DadosConsumoTorre> montarConsumosDasTorres(
            List<Medidor> medidores,
            CalcularRateioRequest request
    ) {
        List<Medidor> medidoresSecundarios = medidores.stream()
                .filter(medidor -> medidor.getTipo() == TipoMedidor.SECUNDARIO)
                .toList();

        if (medidoresSecundarios.isEmpty()) {
            throw new RegraNegocioException("Não existem medidores secundários cadastrados para calcular o rateio.");
        }

        List<DadosConsumoTorre> consumosTorres = medidoresSecundarios.stream()
                .map(medidor -> montarConsumoTorre(medidor, request))
                .toList();

        if (consumosTorres.isEmpty()) {
            throw new RegraNegocioException("Não existem leituras suficientes dos medidores secundários para calcular o rateio.");
        }

        return consumosTorres;
    }

    private DadosConsumoTorre montarConsumoTorre(
            Medidor medidor,
            CalcularRateioRequest request
    ) {
        Long torreId = medidor.getTorreId();

        Torre torre = torreRepository.buscarPorId(torreId)
                .orElseThrow(() -> new RegraNegocioException("Torre não encontrada para o medidor secundário."));

        Leitura leitura = leituraRepository
                .buscarPorMedidorEMes(medidor.getId(), request.mesReferencia())
                .orElseThrow(() -> new RegraNegocioException(
                        "Leitura não encontrada para o medidor secundário da torre " + torre.getNome() + "."
                ));

        return new DadosConsumoTorre(
                torre.getId(),
                torre.getNome(),
                medidor.getId(),
                leitura.getConsumo()
        );
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