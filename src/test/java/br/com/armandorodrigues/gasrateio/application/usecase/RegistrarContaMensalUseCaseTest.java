package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalRequest;
import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.ContaMensal;
import br.com.armandorodrigues.gasrateio.domain.repository.ContaMensalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrarContaMensalUseCaseTest {

    @Mock
    private ContaMensalRepository contaMensalRepository;

    private RegistrarContaMensalUseCase registrarContaMensalUseCase;

    @BeforeEach
    void setUp() {
        registrarContaMensalUseCase = new RegistrarContaMensalUseCase(contaMensalRepository);
    }

    @Test
    void deveRegistrarContaMensalComSucesso() {
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        ContaMensalRequest request = new ContaMensalRequest(
                mesReferencia,
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta referente ao consumo de junho."
        );

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        when(contaMensalRepository.salvar(any(ContaMensal.class)))
                .thenAnswer(invocation -> {
                    ContaMensal conta = invocation.getArgument(0);

                    return new ContaMensal(
                            1L,
                            conta.getMesReferencia(),
                            conta.getValorTotal(),
                            conta.getConsumoInformado(),
                            conta.getDataVencimento(),
                            conta.getNumeroFatura(),
                            conta.getObservacoes()
                    );
                });

        ContaMensalResponse response = registrarContaMensalUseCase.executar(request);

        assertEquals(1L, response.id());
        assertEquals(mesReferencia, response.mesReferencia());
        assertBigDecimalEquals("10000.00", response.valorTotal());
        assertBigDecimalEquals("1200.00", response.consumoInformado());
        assertEquals(LocalDate.of(2026, 7, 10), response.dataVencimento());
        assertEquals("FAT-2026-06", response.numeroFatura());
        assertEquals("Conta referente ao consumo de junho.", response.observacoes());

        verify(contaMensalRepository).buscarPorMesReferencia(mesReferencia);
        verify(contaMensalRepository).salvar(any(ContaMensal.class));
    }

    @Test
    void deveBloquearContaMensalDuplicadaParaMesmoMes() {
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        ContaMensalRequest request = new ContaMensalRequest(
                mesReferencia,
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta duplicada para teste."
        );

        ContaMensal contaExistente = new ContaMensal(
                1L,
                mesReferencia,
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta já cadastrada."
        );

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.of(contaExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarContaMensalUseCase.executar(request)
        );

        assertEquals(
                "Já existe uma conta mensal cadastrada para este mês.",
                exception.getMessage()
        );

        verify(contaMensalRepository).buscarPorMesReferencia(mesReferencia);
        verify(contaMensalRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearValorTotalZero() {
        YearMonth mesReferencia = YearMonth.of(2026, 7);

        ContaMensalRequest request = new ContaMensalRequest(
                mesReferencia,
                bd("0.00"),
                bd("1000.00"),
                LocalDate.of(2026, 8, 10),
                "FAT-2026-07",
                "Conta com valor inválido."
        );

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarContaMensalUseCase.executar(request)
        );

        assertEquals(
                "O valor total da conta deve ser maior que zero.",
                exception.getMessage()
        );

        verify(contaMensalRepository).buscarPorMesReferencia(mesReferencia);
        verify(contaMensalRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearConsumoInformadoNegativo() {
        YearMonth mesReferencia = YearMonth.of(2026, 7);

        ContaMensalRequest request = new ContaMensalRequest(
                mesReferencia,
                bd("10000.00"),
                bd("-1.00"),
                LocalDate.of(2026, 8, 10),
                "FAT-2026-07",
                "Conta com consumo inválido."
        );

        when(contaMensalRepository.buscarPorMesReferencia(mesReferencia))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarContaMensalUseCase.executar(request)
        );

        assertEquals(
                "O consumo informado não pode ser negativo.",
                exception.getMessage()
        );

        verify(contaMensalRepository).buscarPorMesReferencia(mesReferencia);
        verify(contaMensalRepository, never()).salvar(any());
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