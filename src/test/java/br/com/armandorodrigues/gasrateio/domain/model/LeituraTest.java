package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

class LeituraTest {

    @Test
    void deveCriarLeituraECalcularConsumoAutomaticamente() {
        Leitura leitura = Leitura.nova(
                1L,
                YearMonth.of(2026, 6),
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1250.00")
        );

        assertNull(leitura.getId());
        assertEquals(1L, leitura.getMedidorId());
        assertEquals(YearMonth.of(2026, 6), leitura.getMesReferencia());
        assertEquals(LocalDate.of(2026, 6, 30), leitura.getDataLeitura());

        assertBigDecimalEquals("1000.00", leitura.getLeituraAnterior());
        assertBigDecimalEquals("1250.00", leitura.getLeituraAtual());
        assertBigDecimalEquals("250.00", leitura.getConsumo());
    }

    @Test
    void devePermitirConsumoZeroQuandoLeituraAtualForIgualAnterior() {
        Leitura leitura = Leitura.nova(
                1L,
                YearMonth.of(2026, 6),
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1000.00")
        );

        assertBigDecimalEquals("0.00", leitura.getConsumo());
    }

    @Test
    void deveBloquearLeituraAtualMenorQueLeituraAnterior() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        1L,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("1250.00"),
                        bd("1000.00")
                )
        );

        assertEquals(
                "A leitura atual não pode ser menor que a leitura anterior.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearLeituraAnteriorNegativa() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        1L,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("-1.00"),
                        bd("1000.00")
                )
        );

        assertEquals(
                "A leitura anterior não pode ser negativa.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearLeituraAtualNegativa() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        1L,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("1000.00"),
                        bd("-1.00")
                )
        );

        assertEquals(
                "A leitura atual não pode ser negativa.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearMedidorNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        null,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("1000.00"),
                        bd("1250.00")
                )
        );

        assertEquals(
                "O medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearMesReferenciaNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        1L,
                        null,
                        LocalDate.of(2026, 6, 30),
                        bd("1000.00"),
                        bd("1250.00")
                )
        );

        assertEquals(
                "O mês de referência é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearDataLeituraNula() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Leitura.nova(
                        1L,
                        YearMonth.of(2026, 6),
                        null,
                        bd("1000.00"),
                        bd("1250.00")
                )
        );

        assertEquals(
                "A data da leitura é obrigatória.",
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