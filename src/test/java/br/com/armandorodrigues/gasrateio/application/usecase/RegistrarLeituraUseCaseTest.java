package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.LeituraRequest;
import br.com.armandorodrigues.gasrateio.application.dto.LeituraResponse;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.domain.model.Leitura;
import br.com.armandorodrigues.gasrateio.domain.model.Medidor;
import br.com.armandorodrigues.gasrateio.domain.repository.LeituraRepository;
import br.com.armandorodrigues.gasrateio.domain.repository.MedidorRepository;
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
class RegistrarLeituraUseCaseTest {

    @Mock
    private LeituraRepository leituraRepository;

    @Mock
    private MedidorRepository medidorRepository;

    private RegistrarLeituraUseCase registrarLeituraUseCase;

    @BeforeEach
    void setUp() {
        registrarLeituraUseCase = new RegistrarLeituraUseCase(
                leituraRepository,
                medidorRepository
        );
    }

    @Test
    void deveRegistrarLeituraComSucessoECalcularConsumo() {
        Long medidorId = 1L;
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        LeituraRequest request = new LeituraRequest(
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1250.00")
        );

        Medidor medidor = mock(Medidor.class);

        when(medidorRepository.buscarPorId(medidorId))
                .thenReturn(Optional.of(medidor));

        when(leituraRepository.buscarPorMedidorEMes(medidorId, mesReferencia))
                .thenReturn(Optional.empty());

        when(leituraRepository.salvar(any(Leitura.class)))
                .thenAnswer(invocation -> {
                    Leitura leitura = invocation.getArgument(0);

                    return new Leitura(
                            10L,
                            leitura.getMedidorId(),
                            leitura.getMesReferencia(),
                            leitura.getDataLeitura(),
                            leitura.getLeituraAnterior(),
                            leitura.getLeituraAtual()
                    );
                });

        LeituraResponse response = registrarLeituraUseCase.executar(request);

        assertEquals(10L, response.id());
        assertEquals(medidorId, response.medidorId());
        assertEquals(mesReferencia, response.mesReferencia());
        assertEquals(LocalDate.of(2026, 6, 30), response.dataLeitura());

        assertBigDecimalEquals("1000.00", response.leituraAnterior());
        assertBigDecimalEquals("1250.00", response.leituraAtual());
        assertBigDecimalEquals("250.00", response.consumo());

        verify(medidorRepository).buscarPorId(medidorId);
        verify(leituraRepository).buscarPorMedidorEMes(medidorId, mesReferencia);
        verify(leituraRepository).salvar(any(Leitura.class));
    }

    @Test
    void deveBloquearQuandoMedidorNaoExistir() {
        Long medidorId = 99L;
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        LeituraRequest request = new LeituraRequest(
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1250.00")
        );

        when(medidorRepository.buscarPorId(medidorId))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarLeituraUseCase.executar(request)
        );

        assertEquals(
                "Medidor não encontrado para o ID informado.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorId(medidorId);
        verify(leituraRepository, never()).buscarPorMedidorEMes(any(), any());
        verify(leituraRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearLeituraDuplicadaParaMesmoMedidorEMes() {
        Long medidorId = 1L;
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        LeituraRequest request = new LeituraRequest(
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1250.00")
        );

        Medidor medidor = mock(Medidor.class);

        Leitura leituraExistente = Leitura.nova(
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 6, 30),
                bd("1000.00"),
                bd("1250.00")
        );

        when(medidorRepository.buscarPorId(medidorId))
                .thenReturn(Optional.of(medidor));

        when(leituraRepository.buscarPorMedidorEMes(medidorId, mesReferencia))
                .thenReturn(Optional.of(leituraExistente));

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarLeituraUseCase.executar(request)
        );

        assertEquals(
                "Já existe uma leitura cadastrada para este medidor neste mês.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorId(medidorId);
        verify(leituraRepository).buscarPorMedidorEMes(medidorId, mesReferencia);
        verify(leituraRepository, never()).salvar(any());
    }

    @Test
    void deveBloquearLeituraAtualMenorQueLeituraAnterior() {
        Long medidorId = 1L;
        YearMonth mesReferencia = YearMonth.of(2026, 6);

        LeituraRequest request = new LeituraRequest(
                medidorId,
                mesReferencia,
                LocalDate.of(2026, 6, 30),
                bd("1250.00"),
                bd("1000.00")
        );

        Medidor medidor = mock(Medidor.class);

        when(medidorRepository.buscarPorId(medidorId))
                .thenReturn(Optional.of(medidor));

        when(leituraRepository.buscarPorMedidorEMes(medidorId, mesReferencia))
                .thenReturn(Optional.empty());

        RegraNegocioException exception = assertThrows(
                RegraNegocioException.class,
                () -> registrarLeituraUseCase.executar(request)
        );

        assertEquals(
                "A leitura atual não pode ser menor que a leitura anterior.",
                exception.getMessage()
        );

        verify(medidorRepository).buscarPorId(medidorId);
        verify(leituraRepository).buscarPorMedidorEMes(medidorId, mesReferencia);
        verify(leituraRepository, never()).salvar(any());
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