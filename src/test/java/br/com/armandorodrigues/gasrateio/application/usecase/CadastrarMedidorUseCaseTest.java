package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorRequest;
import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import br.com.armandorodrigues.gasrateio.domain.model.Torre;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
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
class CadastrarMedidorUseCaseTest {

    @Mock
    private MedidorRepository medidorRepository;

    @Mock
    private TorreRepository torreRepository;

    private CadastrarMedidorUseCase cadastrarMedidorUseCase;

    @BeforeEach
    void setUp() {
        cadastrarMedidorUseCase = new CadastrarMedidorUseCase(
                medidorRepository,
                torreRepository
        );
    }

    @Test
    void deveCadastrarMedidorPrincipalComSucesso() {
        MedidorRequest request = new MedidorRequest(
                "Medidor Principal",
                "GAS-PRINCIPAL-001",
                TipoMedidor.PRINCIPAL,
                null
        );

        when(medidorRepository.buscarPorCodigo("GAS-PRINCIPAL-001"))
                .thenReturn(Optional.empty());

        when(medidorRepository.salvar(any(Medidor.class)))
                .thenAnswer(invocation -> {
                    Medidor medidor = invocation.getArgument(0);

                    return new Medidor(
                            1L,
                            medidor.getNome(),
                            medidor.getCodigo(),
                            medidor.getTipo(),
                            medidor.getTorreId(),
                            medidor.isAtivo()
                    );
                });

        MedidorResponse response = cadastrarMedidorUseCase.executar(request);

        assertEquals(1L, response.id());
        assertEquals("Medidor Principal", response.nome());
        assertEquals("GAS-PRINCIPAL-001", response.codigo());
        assertEquals(TipoMedidor.PRINCIPAL, response.tipo());
        assertNull(response.torreId());
        assertTrue(response.ativo());

        verify(medidorRepository).buscarPorCodigo("GAS-PRINCIPAL-001");
        verify(torreRepository, never()).buscarPorId(any());
        verify(medidorRepository).salvar(any(Medidor.class));
    }

    @Test
    void deveCadastrarMedidorSecundarioComSucesso() {
        Long torreId = 1L;

        MedidorRequest request = new MedidorRequest(
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                torreId
        );

        Torre torre = mock(Torre.class);

        when(medidorRepository.buscarPorCodigo("GAS-PRIME-001"))
                .thenReturn(Optional.empty());

        when(torreRepository.buscarPorId(torreId))
                .thenReturn(Optional.of(torre));

        when(medidorRepository.salvar(any(Medidor.class)))
                .thenAnswer(invocation -> {
                    Medidor medidor = invocation.getArgument(0);

                    return new Medidor(
                            2L,
                            medidor.getNome(),
                            medidor.getCodigo(),
                            medidor.getTipo(),
                            medidor.getTorreId(),
                            medidor.isAtivo()
                    );
                });

        MedidorResponse response = cadastrarMedidorUseCase.executar(request);

        assertEquals(2L, response.id());
        assertEquals("Medidor Torre Prime", response.nome());
        assertEquals("GAS-PRIME-001", response.codigo());
        assertEquals(TipoMedidor.SECUNDARIO, response.tipo());
        assertEquals(torreId, response.torreId());
        assertTrue(response.ativo());

        verify(medidorRepository).buscarPorCodigo("GAS-PRIME-001");
        verify(torreRepository).buscarPorId(torreId);
        verify(medidorRepository).salvar(any(Medidor.class));
    }

    @Test
    void deveBloquearCodigoDuplicado() {
        MedidorRequest request = new MedidorRequest(
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                1L
        );

        Medidor medidorExistente = new Medidor(
                1L,
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                1L,
                true
        );

        when(medidorRepository.buscarPorCodigo("GAS-PRIME-001"))
                .thenReturn(Optional.of(medidorExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarMedidorUseCase.executar(request)
        );

        assertEquals(
                "Já existe um medidor cadastrado com este código.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorCodigo("GAS-PRIME-001");
        verify(torreRepository, never()).buscarPorId(any());
        verify(medidorRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearMedidorSecundarioComTorreInexistente() {
        Long torreId = 99L;

        MedidorRequest request = new MedidorRequest(
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                torreId
        );

        when(medidorRepository.buscarPorCodigo("GAS-PRIME-001"))
                .thenReturn(Optional.empty());

        when(torreRepository.buscarPorId(torreId))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarMedidorUseCase.executar(request)
        );

        assertEquals(
                "Torre não encontrada para o ID informado.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorCodigo("GAS-PRIME-001");
        verify(torreRepository).buscarPorId(torreId);
        verify(medidorRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearMedidorPrincipalComTorre() {
        MedidorRequest request = new MedidorRequest(
                "Medidor Principal",
                "GAS-PRINCIPAL-001",
                TipoMedidor.PRINCIPAL,
                1L
        );

        when(medidorRepository.buscarPorCodigo("GAS-PRINCIPAL-001"))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> cadastrarMedidorUseCase.executar(request)
        );

        assertEquals(
                "Medidor principal não deve estar vinculado a uma torre.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorCodigo("GAS-PRINCIPAL-001");
        verify(torreRepository, never()).buscarPorId(any());
        verify(medidorRepository, never()).salvar(any());
    }
}