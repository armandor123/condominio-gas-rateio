package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarRateiosUseCaseTest {

    @Mock
    private RateioRepository rateioRepository;

    private ListarRateiosUseCase listarRateiosUseCase;

    @BeforeEach
    void setUp() {
        listarRateiosUseCase = new ListarRateiosUseCase(rateioRepository);
    }

    @Test
    void deveListarRateiosCalculados() {
        Rateio rateioJunho = criarRateio(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1000.00"),
                bd("1200.00"),
                bd("200.00")
        );

        Rateio rateioMaio = criarRateio(
                2L,
                YearMonth.of(2026, 5),
                bd("8500.00"),
                bd("850.00"),
                bd("1000.00"),
                bd("150.00")
        );

        when(rateioRepository.listarTodos())
                .thenReturn(List.of(rateioJunho, rateioMaio));

        List<RateioResponse> response = listarRateiosUseCase.executar();

        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals(YearMonth.of(2026, 6), response.get(0).mesReferencia());
        assertEquals(1L, response.get(0).contaMensalId());
        assertBigDecimalEquals("10000.00", response.get(0).valorTotalConta());
        assertBigDecimalEquals("1000.00", response.get(0).consumoTotalSecundario());
        assertBigDecimalEquals("1200.00", response.get(0).consumoMedidorPrincipal());
        assertBigDecimalEquals("200.00", response.get(0).diferencaConsumo());

        assertEquals(2, response.get(0).itens().size());

        assertEquals("Prime", response.get(0).itens().get(0).nomeTorre());
        assertBigDecimalEquals("600.00", response.get(0).itens().get(0).consumo());
        assertBigDecimalEquals("60.00", response.get(0).itens().get(0).percentual());
        assertBigDecimalEquals("6000.00", response.get(0).itens().get(0).valorRateado());

        assertEquals("Hype", response.get(0).itens().get(1).nomeTorre());
        assertBigDecimalEquals("400.00", response.get(0).itens().get(1).consumo());
        assertBigDecimalEquals("40.00", response.get(0).itens().get(1).percentual());
        assertBigDecimalEquals("4000.00", response.get(0).itens().get(1).valorRateado());

        assertEquals(2L, response.get(1).id());
        assertEquals(YearMonth.of(2026, 5), response.get(1).mesReferencia());
        assertEquals(1L, response.get(1).contaMensalId());
        assertBigDecimalEquals("8500.00", response.get(1).valorTotalConta());
        assertBigDecimalEquals("850.00", response.get(1).consumoTotalSecundario());
        assertBigDecimalEquals("1000.00", response.get(1).consumoMedidorPrincipal());
        assertBigDecimalEquals("150.00", response.get(1).diferencaConsumo());

        verify(rateioRepository).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremRateios() {
        when(rateioRepository.listarTodos())
                .thenReturn(List.of());

        List<RateioResponse> response = listarRateiosUseCase.executar();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(rateioRepository).listarTodos();
    }

    private Rateio criarRateio(
            Long id,
            YearMonth mesReferencia,
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            BigDecimal consumoMedidorPrincipal,
            BigDecimal diferencaConsumo
    ) {
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
                id,
                mesReferencia,
                1L,
                valorTotalConta,
                consumoTotalSecundario,
                consumoMedidorPrincipal,
                diferencaConsumo,
                LocalDateTime.of(2026, mesReferencia.getMonthValue(), 30, 10, 0),
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