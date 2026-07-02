package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.dto.ItemRateioResponse;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.application.service.NoOpRateioEmailService;
import br.com.armandorodrigues.gasrateio.application.service.RateioEmailService;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Leitura;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.ContaMensalRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.LeituraRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.RateioRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio.DadosConsumoTorre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
public class CalcularRateioUseCase {

    private final ContaMensalRepository contaMensalRepository;
    private final LeituraRepository leituraRepository;
    private final MedidorRepository medidorRepository;
    private final TorreRepository torreRepository;
    private final RateioRepository rateioRepository;
    private final RateioEmailService rateioEmailService;
    private final CalculadoraRateio calculadoraRateio;

    public CalcularRateioUseCase(
            ContaMensalRepository contaMensalRepository,
            LeituraRepository leituraRepository,
            MedidorRepository medidorRepository,
            TorreRepository torreRepository,
            RateioRepository rateioRepository
    ) {
        this(
                contaMensalRepository,
                leituraRepository,
                medidorRepository,
                torreRepository,
                rateioRepository,
                new NoOpRateioEmailService()
        );
    }

    @Autowired
    public CalcularRateioUseCase(
            ContaMensalRepository contaMensalRepository,
            LeituraRepository leituraRepository,
            MedidorRepository medidorRepository,
            TorreRepository torreRepository,
            RateioRepository rateioRepository,
            RateioEmailService rateioEmailService
    ) {
        this.contaMensalRepository = contaMensalRepository;
        this.leituraRepository = leituraRepository;
        this.medidorRepository = medidorRepository;
        this.torreRepository = torreRepository;
        this.rateioRepository = rateioRepository;
        this.rateioEmailService = rateioEmailService;
        this.calculadoraRateio = new CalculadoraRateio();
    }

    public RateioResponse executar(CalcularRateioRequest request) {
        YearMonth mesReferencia = request.mesReferencia();

        rateioRepository.buscarPorMesReferencia(mesReferencia)
                .ifPresent(rateio -> {
                    throw new RegraNegocioException("Já existe um rateio calculado para este mês.");
                });

        ContaMensal contaMensal = contaMensalRepository.buscarPorMesReferencia(mesReferencia)
                .orElseThrow(() -> new RegraNegocioException("Conta mensal não encontrada para o mês informado."));

        List<Medidor> medidores = medidorRepository.listarTodos();

        Medidor medidorPrincipal = medidores.stream()
                .filter(Medidor::isAtivo)
                .filter(medidor -> medidor.getTipo() == TipoMedidor.PRINCIPAL)
                .findFirst()
                .orElseThrow(() -> new RegraNegocioException("Medidor principal não encontrado."));

        Leitura leituraPrincipal = leituraRepository
                .buscarPorMedidorEMes(medidorPrincipal.getId(), mesReferencia)
                .orElseThrow(() -> new RegraNegocioException("Leitura do medidor principal não encontrada para o mês informado."));

        List<DadosConsumoTorre> consumosTorres = medidores.stream()
                .filter(Medidor::isAtivo)
                .filter(medidor -> medidor.getTipo() == TipoMedidor.SECUNDARIO)
                .map(medidor -> montarDadosConsumoTorre(medidor, mesReferencia))
                .toList();

        Rateio rateio = calculadoraRateio.calcular(
                contaMensal,
                leituraPrincipal.getConsumo(),
                consumosTorres
        );

        Rateio rateioSalvo = rateioRepository.salvar(rateio);

        rateioEmailService.enviarRateioCalculado(rateioSalvo);

        return toResponse(rateioSalvo);
    }

    private DadosConsumoTorre montarDadosConsumoTorre(
            Medidor medidor,
            YearMonth mesReferencia
    ) {
        Leitura leitura = leituraRepository
                .buscarPorMedidorEMes(medidor.getId(), mesReferencia)
                .orElseThrow(() -> new RegraNegocioException("Leitura do medidor secundário não encontrada para o mês informado."));

        Torre torre = torreRepository.buscarPorId(medidor.getTorreId())
                .orElseThrow(() -> new RegraNegocioException("Torre não encontrada para o ID informado."));

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
                rateio.getItens()
                        .stream()
                        .map(this::toItemResponse)
                        .toList()
        );
    }

    private ItemRateioResponse toItemResponse(ItemRateio item) {
        return new ItemRateioResponse(
                item.getId(),
                item.getTorreId(),
                item.getNomeTorre(),
                item.getMedidorId(),
                item.getConsumo(),
                item.getPercentual(),
                item.getValorRateado()
        );
    }
}
