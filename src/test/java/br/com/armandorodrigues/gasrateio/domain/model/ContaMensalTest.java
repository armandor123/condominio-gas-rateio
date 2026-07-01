package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class ContaMensalTest {

    @Test
    void deveCriarContaMensalComDadosValidos() {
        ContaMensal contaMensal = ContaMensal.nova(
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta referente ao consumo de junho."
        );

        assertNull(contaMensal.getId());
        assertEquals(YearMonth.of(2026, 6), contaMensal.getMesReferencia());
        assertBigDecimalEquals("10000.00", contaMensal.getValorTotal());
        assertBigDecimalEquals("1200.00", contaMensal.getConsumoInformado());
        assertEquals(LocalDate.of(2026, 7, 10), contaMensal.getDataVencimento());
        assertEquals("FAT-2026-06", contaMensal.getNumeroFatura());
        assertEquals("Conta referente ao consumo de junho.", contaMensal.getObservacoes());
    }

    @Test
    void deveNormalizarNumeroFaturaEObservacoes() {
        ContaMensal contaMensal = ContaMensal.nova(
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "   FAT-2026-06   ",
                "   Conta com espaços extras.   "
        );

        assertEquals("FAT-2026-06", contaMensal.getNumeroFatura());
        assertEquals("Conta com espaços extras.", contaMensal.getObservacoes());
    }

    @Test
    void deveConverterTextosOpcionaisVaziosParaNulo() {
        ContaMensal contaMensal = ContaMensal.nova(
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "   ",
                ""
        );

        assertNull(contaMensal.getNumeroFatura());
        assertNull(contaMensal.getObservacoes());
    }

    @Test
    void deveBloquearMesReferenciaNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        null,
                        bd("10000.00"),
                        bd("1200.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O mês de referência é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearValorTotalNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        null,
                        bd("1200.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O valor total da conta é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearValorTotalZero() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        bd("0.00"),
                        bd("1200.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O valor total da conta deve ser maior que zero.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearValorTotalNegativo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        bd("-100.00"),
                        bd("1200.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O valor total da conta deve ser maior que zero.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearConsumoInformadoNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        bd("10000.00"),
                        null,
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O consumo informado é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearConsumoInformadoNegativo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        bd("10000.00"),
                        bd("-1.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "O consumo informado não pode ser negativo.",
                exception.getMessage()
        );
    }

    @Test
    void devePermitirConsumoInformadoZero() {
        ContaMensal contaMensal = ContaMensal.nova(
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("0.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta de teste."
        );

        assertBigDecimalEquals("0.00", contaMensal.getConsumoInformado());
    }

    @Test
    void deveBloquearDataVencimentoNula() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> ContaMensal.nova(
                        YearMonth.of(2026, 6),
                        bd("10000.00"),
                        bd("1200.00"),
                        null,
                        "FAT-2026-06",
                        "Conta de teste."
                )
        );

        assertEquals(
                "A data de vencimento é obrigatória.",
                exception.getMessage()
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