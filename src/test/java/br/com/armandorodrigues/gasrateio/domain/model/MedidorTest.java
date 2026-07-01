package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MedidorTest {

    @Test
    void deveCriarMedidorPrincipalValido() {
        Medidor medidor = Medidor.novoPrincipal(
                "Medidor Principal",
                "GAS-PRINCIPAL-001"
        );

        assertNull(medidor.getId());
        assertEquals("Medidor Principal", medidor.getNome());
        assertEquals("GAS-PRINCIPAL-001", medidor.getCodigo());
        assertEquals(TipoMedidor.PRINCIPAL, medidor.getTipo());
        assertNull(medidor.getTorreId());
        assertTrue(medidor.isAtivo());
    }

    @Test
    void deveCriarMedidorSecundarioValido() {
        Medidor medidor = Medidor.novoSecundario(
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                1L
        );

        assertNull(medidor.getId());
        assertEquals("Medidor Torre Prime", medidor.getNome());
        assertEquals("GAS-PRIME-001", medidor.getCodigo());
        assertEquals(TipoMedidor.SECUNDARIO, medidor.getTipo());
        assertEquals(1L, medidor.getTorreId());
        assertTrue(medidor.isAtivo());
    }

    @Test
    void deveNormalizarNomeECodigoRemovendoEspacosExtras() {
        Medidor medidor = Medidor.novoSecundario(
                "   Medidor Torre Hype   ",
                "   GAS-HYPE-001   ",
                2L
        );

        assertEquals("Medidor Torre Hype", medidor.getNome());
        assertEquals("GAS-HYPE-001", medidor.getCodigo());
    }

    @Test
    void deveBloquearNomeNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        null,
                        "GAS-PRINCIPAL-001"
                )
        );

        assertEquals(
                "O nome do medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearNomeVazio() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        "   ",
                        "GAS-PRINCIPAL-001"
                )
        );

        assertEquals(
                "O nome do medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearNomeComMenosDeDoisCaracteres() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        "A",
                        "GAS-PRINCIPAL-001"
                )
        );

        assertEquals(
                "O nome do medidor deve ter pelo menos 2 caracteres.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearCodigoNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        "Medidor Principal",
                        null
                )
        );

        assertEquals(
                "O código do medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearCodigoVazio() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        "Medidor Principal",
                        "   "
                )
        );

        assertEquals(
                "O código do medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearCodigoComMenosDeDoisCaracteres() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoPrincipal(
                        "Medidor Principal",
                        "A"
                )
        );

        assertEquals(
                "O código do medidor deve ter pelo menos 2 caracteres.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearTipoNulo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> new Medidor(
                        null,
                        "Medidor Teste",
                        "GAS-TESTE-001",
                        null,
                        null,
                        true
                )
        );

        assertEquals(
                "O tipo do medidor é obrigatório.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearMedidorPrincipalVinculadoATorre() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> new Medidor(
                        null,
                        "Medidor Principal",
                        "GAS-PRINCIPAL-001",
                        TipoMedidor.PRINCIPAL,
                        1L,
                        true
                )
        );

        assertEquals(
                "Medidor principal não deve estar vinculado a uma torre.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearMedidorSecundarioSemTorre() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoSecundario(
                        "Medidor Torre Prime",
                        "GAS-PRIME-001",
                        null
                )
        );

        assertEquals(
                "Medidor secundário deve estar vinculado a uma torre.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearTorreComIdZero() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoSecundario(
                        "Medidor Torre Prime",
                        "GAS-PRIME-001",
                        0L
                )
        );

        assertEquals(
                "O ID da torre deve ser válido.",
                exception.getMessage()
        );
    }

    @Test
    void deveBloquearTorreComIdNegativo() {
        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> Medidor.novoSecundario(
                        "Medidor Torre Prime",
                        "GAS-PRIME-001",
                        -1L
                )
        );

        assertEquals(
                "O ID da torre deve ser válido.",
                exception.getMessage()
        );
    }

    @Test
    void deveCriarMedidorUsandoMetodoGenericoNovo() {
        Medidor medidor = Medidor.novo(
                "Medidor Torre Hype",
                "GAS-HYPE-001",
                TipoMedidor.SECUNDARIO,
                2L
        );

        assertNull(medidor.getId());
        assertEquals("Medidor Torre Hype", medidor.getNome());
        assertEquals("GAS-HYPE-001", medidor.getCodigo());
        assertEquals(TipoMedidor.SECUNDARIO, medidor.getTipo());
        assertEquals(2L, medidor.getTorreId());
        assertTrue(medidor.isAtivo());
    }
}