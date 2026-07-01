package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarTorresUseCaseTest {

    @Mock
    private TorreRepository torreRepository;

    private ListarTorresUseCase listarTorresUseCase;

    @BeforeEach
    void setUp() {
        listarTorresUseCase = new ListarTorresUseCase(torreRepository);
    }

    @Test
    void deveListarTorresCadastradas() {
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

        when(torreRepository.listarTodas())
                .thenReturn(List.of(torrePrime, torreHype));

        List<TorreResponse> response = listarTorresUseCase.executar();

        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals("Prime", response.get(0).nome());
        assertTrue(response.get(0).ativa());

        assertEquals(2L, response.get(1).id());
        assertEquals("Hype", response.get(1).nome());
        assertTrue(response.get(1).ativa());

        verify(torreRepository).listarTodas();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremTorres() {
        when(torreRepository.listarTodas())
                .thenReturn(List.of());

        List<TorreResponse> response = listarTorresUseCase.executar();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(torreRepository).listarTodas();
    }
}