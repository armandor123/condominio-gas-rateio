package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TorreTest {

    @Test
    void deveCriarTorreValida() {
        Torre torre = Torre.nova("Prime");

        assertNull(torre.getId());
        assertEquals("Prime", torre.getNome());
        assertTrue(torre.isAtiva());
    }

    @Test
    void deveNormalizarNomeRemovendoEspacosExtras() {
        Torre torre = Torre.nova("   Hype   ");

        assertEquals("Hype", torre.getNome());
        assertTrue(torre.isAtiva());
    }

    @Test
    void deveCriarTorreComConstrutorCompleto() {
        Torre torre = new Torre(
                1L,
                "Prime",
                true
        );

        assertEquals(1L, torre.getId());
        assertEquals("Prime", torre.getNome());
        assertTrue(torre.isAtiva());
    }

    @Test
    void devePermitirTorreInativaQuandoInformadoNoConstrutor() {
        Torre torre = new Torre(
                1L,
                "Prime",
                false
        );

        assertEquals(1L, torre.getId());
        assertEquals("Prime", torre.getNome());
        assertFalse(torre.isAtiva());
    }

    @Test
    void deveBloquearNomeNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Torre.nova(null)
        );

        assertEquals(
                "O nome da torre é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearNomeVazio() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Torre.nova("   ")
        );

        assertEquals(
                "O nome da torre é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearNomeComMenosDeDoisCaracteres() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Torre.nova("A")
        );

        assertEquals(
                "O nome da torre deve ter pelo menos 2 caracteres.",
                exception.getMessage()
        );
    }
}