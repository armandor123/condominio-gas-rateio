package br.com.armandorodrigues.gasrateio.infrastructure.config;

import br.com.armandorodrigues.gasrateio.domain.model.*;
import br.com.armandorodrigues.gasrateio.domain.repository.*;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio.DadosConsumoTorre;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final TorreRepository torreRepository;
    private final MedidorRepository medidorRepository;
    private final LeituraRepository leituraRepository;
    private final ContaMensalRepository contaMensalRepository;
    private final RateioRepository rateioRepository;
    private final CalculadoraRateio calculadoraRateio = new CalculadoraRateio();

    @Value("${app.seed.demo-enabled:false}")
    private boolean demoEnabled;

    public DataInitializer(
            TorreRepository torreRepository,
            MedidorRepository medidorRepository,
            LeituraRepository leituraRepository,
            ContaMensalRepository contaMensalRepository,
            RateioRepository rateioRepository
    ) {
        this.torreRepository = torreRepository;
        this.medidorRepository = medidorRepository;
        this.leituraRepository = leituraRepository;
        this.contaMensalRepository = contaMensalRepository;
        this.rateioRepository = rateioRepository;
    }

    @Override
    public void run(String... args) {
        if (!demoEnabled) {
            return;
        }

        Torre prime = criarTorreSeNaoExistir("Prime");
        Torre hype = criarTorreSeNaoExistir("Hype");

        Medidor medidorPrincipal = criarMedidorSeNaoExistir(
                "GAS-PRINCIPAL-001",
                "Medidor Principal",
                TipoMedidor.PRINCIPAL,
                null
        );

        Medidor medidorPrime = criarMedidorSeNaoExistir(
                "GAS-PRIME-001",
                "Medidor Torre Prime",
                TipoMedidor.SECUNDARIO,
                prime.getId()
        );

        Medidor medidorHype = criarMedidorSeNaoExistir(
                "GAS-HYPE-001",
                "Medidor Torre Hype",
                TipoMedidor.SECUNDARIO,
                hype.getId()
        );

        List<DadosMesDemo> mesesDemo = List.of(
                new DadosMesDemo(
                        YearMonth.of(2026, 4),
                        LocalDate.of(2026, 4, 30),
                        bd("9500.00"),
                        bd("1150.00"),
                        LocalDate.of(2026, 5, 10),
                        "FAT-2026-04",
                        bd("7600.00"),
                        bd("8750.00"),
                        bd("3800.00"),
                        bd("4360.00"),
                        bd("3000.00"),
                        bd("3400.00")
                ),
                new DadosMesDemo(
                        YearMonth.of(2026, 5),
                        LocalDate.of(2026, 5, 31),
                        bd("10300.00"),
                        bd("1200.00"),
                        LocalDate.of(2026, 6, 10),
                        "FAT-2026-05",
                        bd("8750.00"),
                        bd("9950.00"),
                        bd("4360.00"),
                        bd("4950.00"),
                        bd("3400.00"),
                        bd("3820.00")
                ),
                new DadosMesDemo(
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("11000.00"),
                        bd("1250.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        bd("9950.00"),
                        bd("11200.00"),
                        bd("4950.00"),
                        bd("5580.00"),
                        bd("3820.00"),
                        bd("4260.00")
                )
        );

        for (DadosMesDemo dadosMes : mesesDemo) {
            criarLeiturasSeNaoExistirem(
                    dadosMes,
                    medidorPrincipal,
                    medidorPrime,
                    medidorHype
            );

            ContaMensal contaMensal = criarContaMensalSeNaoExistir(dadosMes);

            calcularRateioSeNaoExistir(
                    dadosMes,
                    contaMensal,
                    medidorPrincipal,
                    medidorPrime,
                    medidorHype,
                    prime,
                    hype
            );
        }
    }

    private Torre criarTorreSeNaoExistir(String nome) {
        return torreRepository.buscarPorNome(nome)
                .orElseGet(() -> torreRepository.salvar(Torre.nova(nome)));
    }

    private Medidor criarMedidorSeNaoExistir(
            String codigo,
            String nome,
            TipoMedidor tipo,
            Long torreId
    ) {
        return medidorRepository.buscarPorCodigo(codigo)
                .orElseGet(() -> medidorRepository.salvar(
                        Medidor.novo(nome, codigo, tipo, torreId)
                ));
    }

    private void criarLeiturasSeNaoExistirem(
            DadosMesDemo dadosMes,
            Medidor medidorPrincipal,
            Medidor medidorPrime,
            Medidor medidorHype
    ) {
        criarLeituraSeNaoExistir(
                medidorPrincipal.getId(),
                dadosMes.mesReferencia(),
                dadosMes.dataLeitura(),
                dadosMes.leituraPrincipalAnterior(),
                dadosMes.leituraPrincipalAtual()
        );

        criarLeituraSeNaoExistir(
                medidorPrime.getId(),
                dadosMes.mesReferencia(),
                dadosMes.dataLeitura(),
                dadosMes.leituraPrimeAnterior(),
                dadosMes.leituraPrimeAtual()
        );

        criarLeituraSeNaoExistir(
                medidorHype.getId(),
                dadosMes.mesReferencia(),
                dadosMes.dataLeitura(),
                dadosMes.leituraHypeAnterior(),
                dadosMes.leituraHypeAtual()
        );
    }

    private void criarLeituraSeNaoExistir(
            Long medidorId,
            YearMonth mesReferencia,
            LocalDate dataLeitura,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual
    ) {
        leituraRepository.buscarPorMedidorEMes(medidorId, mesReferencia)
                .orElseGet(() -> leituraRepository.salvar(
                        Leitura.nova(
                                medidorId,
                                mesReferencia,
                                dataLeitura,
                                leituraAnterior,
                                leituraAtual
                        )
                ));
    }

    private ContaMensal criarContaMensalSeNaoExistir(DadosMesDemo dadosMes) {
        return contaMensalRepository.buscarPorMesReferencia(dadosMes.mesReferencia())
                .orElseGet(() -> contaMensalRepository.salvar(
                        ContaMensal.nova(
                                dadosMes.mesReferencia(),
                                dadosMes.valorConta(),
                                dadosMes.consumoInformado(),
                                dadosMes.dataVencimento(),
                                dadosMes.numeroFatura(),
                                "Conta mensal demonstrativa criada automaticamente."
                        )
                ));
    }

    private void calcularRateioSeNaoExistir(
            DadosMesDemo dadosMes,
            ContaMensal contaMensal,
            Medidor medidorPrincipal,
            Medidor medidorPrime,
            Medidor medidorHype,
            Torre prime,
            Torre hype
    ) {
        if (rateioRepository.buscarPorMesReferencia(dadosMes.mesReferencia()).isPresent()) {
            return;
        }

        Leitura leituraPrincipal = leituraRepository
                .buscarPorMedidorEMes(medidorPrincipal.getId(), dadosMes.mesReferencia())
                .orElseThrow();

        Leitura leituraPrime = leituraRepository
                .buscarPorMedidorEMes(medidorPrime.getId(), dadosMes.mesReferencia())
                .orElseThrow();

        Leitura leituraHype = leituraRepository
                .buscarPorMedidorEMes(medidorHype.getId(), dadosMes.mesReferencia())
                .orElseThrow();

        Rateio rateio = calculadoraRateio.calcular(
                contaMensal,
                leituraPrincipal.getConsumo(),
                List.of(
                        new DadosConsumoTorre(
                                prime.getId(),
                                prime.getNome(),
                                medidorPrime.getId(),
                                leituraPrime.getConsumo()
                        ),
                        new DadosConsumoTorre(
                                hype.getId(),
                                hype.getNome(),
                                medidorHype.getId(),
                                leituraHype.getConsumo()
                        )
                )
        );

        rateioRepository.salvar(rateio);
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private record DadosMesDemo(
            YearMonth mesReferencia,
            LocalDate dataLeitura,
            BigDecimal valorConta,
            BigDecimal consumoInformado,
            LocalDate dataVencimento,
            String numeroFatura,
            BigDecimal leituraPrincipalAnterior,
            BigDecimal leituraPrincipalAtual,
            BigDecimal leituraPrimeAnterior,
            BigDecimal leituraPrimeAtual,
            BigDecimal leituraHypeAnterior,
            BigDecimal leituraHypeAtual
    ) {
    }
}