package br.com.armandorodrigues.gasrateio.domain.service;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.model.ItemRateio;
import br.com.armandorodrigues.gasrateio.domain.model.Rateio;
import br.com.armandorodrigues.gasrateio.domain.service.CalculadoraRateio.DadosConsumoTorre;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CalculadoraRateioTest {

    private final CalculadoraRateio calculadoraRateio = new CalculadoraRateio();

    @Test
    void deveCalcularRateioProporcionalEntreDuasTorres() {
        ContaMensal contaMensal = criarContaMensal(
                bd("10000.00"),
                bd("1200.00")
        );

        List<DadosConsumoTorre> consumosTorres = List.of(
                new DadosConsumoTorre(
                        1L,
                        "Prime",
                        2L,
                        bd("600.00")
                ),
                new DadosConsumoTorre(
                        2L,
                        "Hype",
                        3L,
                        bd("400.00")
                )
        );

        Rateio rateio = calculadoraRateio.calcular(
                contaMensal,
                bd("1200.00"),
                consumosTorres
        );

        assertEquals(YearMonth.of(2026, 6), rateio.getMesReferencia());
        assertEquals(1L, rateio.getContaMensalId());

        assertBigDecimalEquals("10000.00", rateio.getValorTotalConta());
        assertBigDecimalEquals("1000.00", rateio.getConsumoTotalSecundario());
        assertBigDecimalEquals("1200.00", rateio.getConsumoMedidorPrincipal());
        assertBigDecimalEquals("200.00", rateio.getDiferencaConsumo());

        assertEquals(2, rateio.getItens().size());

        ItemRateio itemPrime = rateio.getItens().get(0);
        assertEquals(1L, itemPrime.getTorreId());
        assertEquals("Prime", itemPrime.getNomeTorre());
        assertEquals(2L, itemPrime.getMedidorId());
        assertBigDecimalEquals("600.00", itemPrime.getConsumo());
        assertBigDecimalEquals("60.00", itemPrime.getPercentual());
        assertBigDecimalEquals("6000.00", itemPrime.getValorRateado());

        ItemRateio itemHype = rateio.getItens().get(1);
        assertEquals(2L, itemHype.getTorreId());
        assertEquals("Hype", itemHype.getNomeTorre());
        assertEquals(3L, itemHype.getMedidorId());
        assertBigDecimalEquals("400.00", itemHype.getConsumo());
        assertBigDecimalEquals("40.00", itemHype.getPercentual());
        assertBigDecimalEquals("4000.00", itemHype.getValorRateado());
    }

    @Test
    void deveCalcularDiferencaEntreMedidorPrincipalESecundarios() {
        ContaMensal contaMensal = criarContaMensal(
                bd("11000.00"),
                bd("1250.00")
        );

        List<DadosConsumoTorre> consumosTorres = List.of(
                new DadosConsumoTorre(
                        1L,
                        "Prime",
                        2L,
                        bd("630.00")
                ),
                new DadosConsumoTorre(
                        2L,
                        "Hype",
                        3L,
                        bd("440.00")
                )
        );

        Rateio rateio = calculadoraRateio.calcular(
                contaMensal,
                bd("1250.00"),
                consumosTorres
        );

        assertBigDecimalEquals("1070.00", rateio.getConsumoTotalSecundario());
        assertBigDecimalEquals("1250.00", rateio.getConsumoMedidorPrincipal());
        assertBigDecimalEquals("180.00", rateio.getDiferencaConsumo());
    }

    @Test
    void deveAjustarDiferencaDeArredondamentoNoItemDeMaiorConsumo() {
        ContaMensal contaMensal = criarContaMensal(
                bd("100.00"),
                bd("9.00")
        );

        List<DadosConsumoTorre> consumosTorres = List.of(
                new DadosConsumoTorre(
                        1L,
                        "Prime",
                        2L,
                        bd("4.00")
                ),
                new DadosConsumoTorre(
                        2L,
                        "Hype",
                        3L,
                        bd("3.00")
                ),
                new DadosConsumoTorre(
                        3L,
                        "Comercial",
                        4L,
                        bd("2.00")
                )
        );

        Rateio rateio = calculadoraRateio.calcular(
                contaMensal,
                bd("9.00"),
                consumosTorres
        );

        ItemRateio itemPrime = rateio.getItens().get(0);
        ItemRateio itemHype = rateio.getItens().get(1);
        ItemRateio itemComercial = rateio.getItens().get(2);

        assertBigDecimalEquals("44.45", itemPrime.getValorRateado());
        assertBigDecimalEquals("33.33", itemHype.getValorRateado());
        assertBigDecimalEquals("22.22", itemComercial.getValorRateado());

        BigDecimal totalRateado = itemPrime.getValorRateado()
                .add(itemHype.getValorRateado())
                .add(itemComercial.getValorRateado());

        assertBigDecimalEquals("100.00", totalRateado);
    }

    @Test
    void deveBloquearConsumoTotalSecundarioIgualAZero() {
        ContaMensal contaMensal = criarContaMensal(
                bd("10000.00"),
                bd("1200.00")
        );

        List<DadosConsumoTorre> consumosTorres = List.of(
                new DadosConsumoTorre(
                        1L,
                        "Prime",
                        2L,
                        bd("0.00")
                ),
                new DadosConsumoTorre(
                        2L,
                        "Hype",
                        3L,
                        bd("0.00")
                )
        );

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> calculadoraRateio.calcular(
                        contaMensal,
                        bd("1200.00"),
                        consumosTorres
                )
        );

        assertEquals(
                "O consumo total dos medidores secundários deve ser maior que zero.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearListaVaziaDeConsumosDasTorres() {
        ContaMensal contaMensal = criarContaMensal(
                bd("10000.00"),
                bd("1200.00")
        );

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> calculadoraRateio.calcular(
                        contaMensal,
                        bd("1200.00"),
                        List.of()
                )
        );

        assertEquals(
                "Não existem leituras suficientes dos medidores secundários para calcular o rateio.",
                exception.getMessage()
        );
    }

    private ContaMensal criarContaMensal(
            BigDecimal valorTotal,
            BigDecimal consumoInformado
    ) {
        return new ContaMensal(
                1L,
                YearMonth.of(2026, 6),
                valorTotal,
                consumoInformado,
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta mensal usada nos testes."
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