package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.CalcularRateioRequest;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.*;
import br.com.armandorodrigues.gasrateio.domain.repository.*;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalcularRateioUseCaseTest {

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

    private CalcularRateioUseCase calcularRateioUseCase;

    @BeforeEach
    void setUp() {
        calcularRateioUseCase = new CalcularRateioUseCase(
                contaMensalRepository,
                leituraRepository,
                medidorRepository,
                torreRepository,
                rateioRepository
        );
    }

    @Test
    void deveCalcularRateioComSucesso() {
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        ContaMensal contaMensal = criarContaMensal();

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

        Torre torrePrime = mock(Torre.class);
        when(torrePrime.getId()).thenReturn(1L);
        when(torrePrime.getNome()).thenReturn("Prime");

        Torre torreHype = mock(Torre.class);
        when(torreHype.getId()).thenReturn(2L);
        when(torreHype.getNome()).thenReturn("Hype");

        Leitura leituraPrincipal = criarLeitura(
                1L,
                1L,
                bd("10000.00"),
                bd("11200.00")
        );

        Leitura leituraPrime = criarLeitura(
                2L,
                2L,
                bd("5000.00"),
                bd("5600.00")
        );

        Leitura leituraHype = criarLeitura(
                3L,
                3L,
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

        when(torreRepository.buscarPorId(1L))
                .thenReturn(Optional.of(torrePrime));

        when(leituraRepository.buscarPorMedidorEMes(2L, mesReferencia))
                .thenReturn(Optional.of(leituraPrime));

        when(torreRepository.buscarPorId(2L))
                .thenReturn(Optional.of(torreHype));

        when(leituraRepository.buscarPorMedidorEMes(3L, mesReferencia))
                .thenReturn(Optional.of(leituraHype));

        when(rateioRepository.salvar(any(Rateio.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RateioResponse response = calcularRateioUseCase.executar(
                new CalcularRateioRequest(mesReferencia)
        );

        assertEquals(mesReferencia, response.mesReferencia());
        assertEquals(1L, response.contaMensalId());

        assertBigDecimalEquals("10000.00", response.valorTotalConta());
        assertBigDecimalEquals("1000.00", response.consumoTotalSecundario());
        assertBigDecimalEquals("1200.00", response.consumoMedidorPrincipal());
        assertBigDecimalEquals("200.00", response.diferencaConsumo());

        assertEquals(2, response.itens().size());

        assertEquals("Prime", response.itens().get(0).nomeTorre());
        assertBigDecimalEquals("600.00", response.itens().get(0).consumo());
        assertBigDecimalEquals("60.00", response.itens().get(0).percentual());
        assertBigDecimalEquals("6000.00", response.itens().get(0).valorRateado());

        assertEquals("Hype", response.itens().get(1).nomeTorre());
        assertBigDecimalEquals("400.00", response.itens().get(1).consumo());
        assertBigDecimalEquals("40.00", response.itens().get(1).percentual());
        assertBigDecimalEquals("4000.00", response.itens().get(1).valorRateado());

        verify(rateioRepository).salvar(any(Rateio.class));
    }

    @Test
    void deveBloquearRateioDuplicado() {
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        Rateio rateioExistente = mock(Rateio.class);

        when(rateioRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.of(rateioExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> calcularRateioUseCase.executar(
                        new CalcularRateioRequest(mesReferencia)
                )
        );

        assertEquals(
                "Já existe um rateio calculado para este mês.",
                exception.getMessage()
        );

        verify(contaMensalRepository, never()).buscarPorMesReferencia(any());
        verify(rateioRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearQuandoContaMensalNaoExistir() {
        YearMonth mesReferencia = YearMonth.of(2026, 7);

        when(rateioRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> calcularRateioUseCase.executar(
                        new CalcularRateioRequest(mesReferencia)
                )
        );

        assertEquals(
                "Conta mensal não encontrada para o mês informado.",
                exception.getMessage()
        );

        verify(medidorRepository, never()).listarTodos();
        verify(rateioRepository, never()).salvar(any());
    }

    private ContaMensal criarContaMensal() {
        return new ContaMensal(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta mensal usada no teste."
        );
    }

    private Leitura criarLeitura(
            Long id,
            Long medidorId,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual
    ) {
        return new Leitura(
                id,
                medidorId,
                YearMonth.of(2026, 6),
                LocalDate.of(2026, 6, 30),
                leituraAnterior,
                leituraAtual
        );
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }

    private void assertBigDecimalEquals(String esperado, BigDecimal atual) {
        assertEquals(
                0,
                new BigDecimal(esperado).compareTo(atual),
                "Esperado: " + esperado + ", mas veio: " + atual
        );
    }
}