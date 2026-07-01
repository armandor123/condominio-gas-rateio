package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.repository.RateioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BuscarRateioPorMesUseCaseTest {

    @Mock
    private RateioRepository rateioRepository;

    private BuscarRateioPorMesUseCase buscarRateioPorMesUseCase;

    @BeforeEach
    void setUp() {
        buscarRateioPorMesUseCase = new BuscarRateioPorMesUseCase(rateioRepository);
    }

    @Test
    void deveBuscarRateioExistentePorMes() {
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        Rateio rateio = criarRateio();

        when(rateioRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.of(rateio));

        RateioResponse response = buscarRateioPorMesUseCase.executar("2026-06");

        assertEquals(1L, response.id());
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

        verify(rateioRepository).buscarPorMesReferencia(mesReferencia);
    }

    @Test
    void deveBloquearMesReferenciaInvalido() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> buscarRateioPorMesUseCase.executar("junho-2026")
        );

        assertEquals(
                "Mês de referência inválido. Use o formato yyyy-MM.",
                exception.getMessage()
        );

        verify(rateioRepository, never()).buscarPorMesReferencia(any());
    }

    @Test
    void deveBloquearQuandoRateioNaoExistirParaOMesInformado() {
        YearMonth mesReferencia = YearMonth.of(2026, 7);

        when(rateioRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> buscarRateioPorMesUseCase.executar("2026-07")
        );

        assertEquals(
                "Rateio não encontrado para o mês informado.",
                exception.getMessage()
        );

        verify(rateioRepository).buscarPorMesReferencia(mesReferencia);
    }

    private Rateio criarRateio() {
        List<ItemRateio> itens = List.of(
                new ItemRateio(
                        1L,
                        1L,
                        "Prime",
                        2L,
                        bd("600.00"),
                        bd("60.00"),
                        bd("6000.00")
                ),
                new ItemRateio(
                        2L,
                        2L,
                        "Hype",
                        3L,
                        bd("400.00"),
                        bd("40.00"),
                        bd("4000.00")
                )
        );

        return new Rateio(
                1L,
                YearMonth.of(2026, 6),
                1L,
                bd("10000.00"),
                bd("1000.00"),
                bd("1200.00"),
                bd("200.00"),
                LocalDateTime.of(2026, 6, 30, 10, 0),
                itens
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