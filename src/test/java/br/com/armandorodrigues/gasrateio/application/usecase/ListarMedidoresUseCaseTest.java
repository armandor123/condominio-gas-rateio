package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarMedidoresUseCaseTest {

    @Mock
    private MedidorRepository medidorRepository;

    private ListarMedidoresUseCase listarMedidoresUseCase;

    @BeforeEach
    void setUp() {
        listarMedidoresUseCase = new ListarMedidoresUseCase(medidorRepository);
    }

    @Test
    void deveListarMedidoresCadastrados() {
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

        when(medidorRepository.listarTodos())
                .thenReturn(List.of(medidorPrincipal, medidorPrime, medidorHype));

        List<MedidorResponse> response = listarMedidoresUseCase.executar();

        assertEquals(3, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals("Medidor Principal", response.get(0).nome());
        assertEquals("GAS-PRINCIPAL-001", response.get(0).codigo());
        assertEquals(TipoMedidor.PRINCIPAL, response.get(0).tipo());
        assertNull(response.get(0).torreId());
        assertTrue(response.get(0).ativo());

        assertEquals(2L, response.get(1).id());
        assertEquals("Medidor Torre Prime", response.get(1).nome());
        assertEquals("GAS-PRIME-001", response.get(1).codigo());
        assertEquals(TipoMedidor.SECUNDARIO, response.get(1).tipo());
        assertEquals(1L, response.get(1).torreId());
        assertTrue(response.get(1).ativo());

        assertEquals(3L, response.get(2).id());
        assertEquals("Medidor Torre Hype", response.get(2).nome());
        assertEquals("GAS-HYPE-001", response.get(2).codigo());
        assertEquals(TipoMedidor.SECUNDARIO, response.get(2).tipo());
        assertEquals(2L, response.get(2).torreId());
        assertTrue(response.get(2).ativo());

        verify(medidorRepository).listarTodos();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremMedidores() {
        when(medidorRepository.listarTodos())
                .thenReturn(List.of());

        List<MedidorResponse> response = listarMedidoresUseCase.executar();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(medidorRepository).listarTodos();
    }
}