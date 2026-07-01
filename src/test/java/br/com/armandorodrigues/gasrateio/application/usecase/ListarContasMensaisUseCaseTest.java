package br.com.armandorodrigues.gasrateio.application.usecase;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ListarContasMensaisUseCaseTest {

    @Mock
    private ContaMensalRepository contaMensalRepository;

    private ListarContasMensaisUseCase listarContasMensaisUseCase;

    @BeforeEach
    void setUp() {
        listarContasMensaisUseCase = new ListarContasMensaisUseCase(contaMensalRepository);
    }

    @Test
    void deveListarContasMensaisCadastradas() {
        ContaMensal contaJunho = new ContaMensal(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta referente ao consumo de junho."
        );

        ContaMensal contaMaio = new ContaMensal(
                2L,
                YearMonth.of(2026, 5),
                bd("8500.00"),
                bd("1000.00"),
                LocalDate.of(2026, 6, 10),
                "FAT-2026-05",
                "Conta referente ao consumo de maio."
        );

        when(contaMensalRepository.listarTodas())
                .thenReturn(List.of(contaJunho, contaMaio));

        List<ContaMensalResponse> response = listarContasMensaisUseCase.executar();

        assertEquals(2, response.size());

        assertEquals(1L, response.get(0).id());
        assertEquals(YearMonth.of(2026, 6), response.get(0).mesReferencia());
        assertBigDecimalEquals("10000.00", response.get(0).valorTotal());
        assertBigDecimalEquals("1200.00", response.get(0).consumoInformado());
        assertEquals(LocalDate.of(2026, 7, 10), response.get(0).dataVencimento());
        assertEquals("FAT-2026-06", response.get(0).numeroFatura());
        assertEquals("Conta referente ao consumo de junho.", response.get(0).observacoes());

        assertEquals(2L, response.get(1).id());
        assertEquals(YearMonth.of(2026, 5), response.get(1).mesReferencia());
        assertBigDecimalEquals("8500.00", response.get(1).valorTotal());
        assertBigDecimalEquals("1000.00", response.get(1).consumoInformado());
        assertEquals(LocalDate.of(2026, 6, 10), response.get(1).dataVencimento());
        assertEquals("FAT-2026-05", response.get(1).numeroFatura());
        assertEquals("Conta referente ao consumo de maio.", response.get(1).observacoes());

        verify(contaMensalRepository).listarTodas();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremContasMensais() {
        when(contaMensalRepository.listarTodas())
                .thenReturn(List.of());

        List<ContaMensalResponse> response = listarContasMensaisUseCase.executar();

        assertNotNull(response);
        assertTrue(response.isEmpty());

        verify(contaMensalRepository).listarTodas();
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