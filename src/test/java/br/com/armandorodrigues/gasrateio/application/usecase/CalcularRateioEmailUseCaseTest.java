package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.service.RateioEmailService;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalcularRateioEmailUseCaseTest {

    @Mock
    private ContaMensalRepository contaMensalRepository;

    @Mock
    private LeituraRepository leituraRepository;

    @Mock
    private MedidorRepository medidorRepository;

    @Mock
    private TorreRepository torreRepository;

    @Mock
    private RateioRepository rateioRepository;

    @Mock
    private RateioEmailService rateioEmailService;

    private CalcularRateioUseCase calcularRateioUseCase;

    @BeforeEach
    void setUp() {
        calcularRateioUseCase = new CalcularRateioUseCase(
                contaMensalRepository,
                leituraRepository,
                medidorRepository,
                torreRepository,
                rateioRepository,
                rateioEmailService
        );
    }

    @Test
    void deveEnviarEmailQuandoRateioForCalculadoComSucesso() {
        YearMonth mesReferencia = YearMonth.of(2026, 8);

        ContaMensal contaMensal = new ContaMensal(
                1L,
                mesReferencia,
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 9, 10),
                "FAT-2026-08",
                "Conta usada no teste."
        );

        Medidor medidorPrincipal = new Medidor(
                1L,
                "Medidor Principal",
                "GAS-PRINCIPAL-001",
                TipoMedidor.PRINCIPAL,
                null,
                true
        );

        Medidor medidorPrime = new Medidor(
                2L,
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                1L,
                true
        );

        Medidor medidorHype = new Medidor(
                3L,
                "Medidor Torre Hype",
                "GAS-HYPE-001",
                TipoMedidor.SECUNDARIO,
                2L,
                true
        );

        Torre torrePrime = new Torre(
                1L,
                "Prime",
                true
        );

        Torre torreHype = new Torre(
                2L,
                "Hype",
                true
        );

        Leitura leituraPrincipal = criarLeitura(
                1L,
                1L,
                mesReferencia,
                bd("10000.00"),
                bd("11200.00")
        );

        Leitura leituraPrime = criarLeitura(
                2L,
                2L,
                mesReferencia,
                bd("5000.00"),
                bd("5600.00")
        );

        Leitura leituraHype = criarLeitura(
                3L,
                3L,
                mesReferencia,
                bd("4000.00"),
                bd("4400.00")
        );

        when(rateioRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.of(contaMensal));

        when(medidorRepository.listarTodos())
                .thenReturn(List.of(medidorPrincipal, medidorPrime, medidorHype));

        when(leituraRepository.buscarPorMedidorEMes(1L, mesReferencia))
                .thenReturn(Optional.of(leituraPrincipal));

        when(leituraRepository.buscarPorMedidorEMes(2L, mesReferencia))
                .thenReturn(Optional.of(leituraPrime));

        when(leituraRepository.buscarPorMedidorEMes(3L, mesReferencia))
                .thenReturn(Optional.of(leituraHype));

        when(torreRepository.buscarPorId(1L))
                .thenReturn(Optional.of(torrePrime));

        when(torreRepository.buscarPorId(2L))
                .thenReturn(Optional.of(torreHype));

        when(rateioRepository.salvar(any(Rateio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        calcularRateioUseCase.executar(
                new CalcularRateioRequest(mesReferencia)
        );

        verify(rateioEmailService).enviarRateioCalculado(any(Rateio.class));
    }

    private Leitura criarLeitura(
            Long id,
            Long medidorId,
            YearMonth mesReferencia,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual
    ) {
        return new Leitura(
                id,
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 8, 31),
                leituraAnterior,
                leituraAtual
        );
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }
}
