package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.TorreRequest;
import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.TorreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CadastrarTorreUseCaseTest {

    @Mock
    private TorreRepository torreRepository;

    private CadastrarTorreUseCase cadastrarTorreUseCase;

    @BeforeEach
    void setUp() {
        cadastrarTorreUseCase = new CadastrarTorreUseCase(torreRepository);
    }

    @Test
    void deveCadastrarTorreComSucesso() {
        TorreRequest request = new TorreRequest("Prime");

        when(torreRepository.buscarPorNome("Prime"))
                .thenReturn(Optional.empty());

        when(torreRepository.salvar(any(Torre.class)))
                .thenAnswer(invocation -> {
                    Torre torre = invocation.getArgument(0);

                    return new Torre(
                            1L,
                            torre.getNome(),
                            torre.isAtiva()
                    );
                });

        TorreResponse response = cadastrarTorreUseCase.executar(request);

        assertEquals(1L, response.id());
        assertEquals("Prime", response.nome());
        assertTrue(response.ativa());

        verify(torreRepository).buscarPorNome("Prime");
        verify(torreRepository).salvar(any(Torre.class));
    }

    @Test
    void deveBloquearTorreDuplicadaPeloNome() {
        TorreRequest request = new TorreRequest("Prime");

        Torre torreExistente = new Torre(
                1L,
                "Prime",
                true
        );

        when(torreRepository.buscarPorNome("Prime"))
                .thenReturn(Optional.of(torreExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarTorreUseCase.executar(request)
        );

        assertEquals(
                "Já existe uma torre cadastrada com este nome.",
                exception.getMessage()
        );

        verify(torreRepository).buscarPorNome("Prime");
        verify(torreRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearNomeNulo() {
        TorreRequest request = new TorreRequest(null);

        when(torreRepository.buscarPorNome(null))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarTorreUseCase.executar(request)
        );

        assertEquals(
                "O nome da torre é obrigatório.",
                exception.getMessage()
        );

        verify(torreRepository).buscarPorNome(null);
        verify(torreRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearNomeVazio() {
        TorreRequest request = new TorreRequest("   ");

        when(torreRepository.buscarPorNome("   "))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarTorreUseCase.executar(request)
        );

        assertEquals(
                "O nome da torre é obrigatório.",
                exception.getMessage()
        );

        verify(torreRepository).buscarPorNome("   ");
        verify(torreRepository, never()).salvar(any());
    }
}