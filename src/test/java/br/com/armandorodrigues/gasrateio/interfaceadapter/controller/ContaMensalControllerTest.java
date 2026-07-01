package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.ContaMensalResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarContasMensaisUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.RegistrarContaMensalUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContaMensalController.class)
class ContaMensalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarContaMensalUseCase registrarContaMensalUseCase;

    @MockitoBean
    private ListarContasMensaisUseCase listarContasMensaisUseCase;

    @Test
    void deveRegistrarContaMensalComSucesso() throws Exception {
        ContaMensalResponse response = new ContaMensalResponse(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1200.00"),
                LocalDate.of(2026, 7, 10),
                "FAT-2026-06",
                "Conta referente ao consumo de junho."
        );

        when(registrarContaMensalUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/contas-mensais")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mesReferencia": "2026-06",
                                  "valorTotal": 10000.00,
                                  "consumoInformado": 1200.00,
                                  "dataVencimento": "2026-07-10",
                                  "numeroFatura": "FAT-2026-06",
                                  "observacoes": "Conta referente ao consumo de junho."
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$.valorTotal").value(10000.00))
                .andExpect(jsonPath("$.consumoInformado").value(1200.00))
                .andExpect(jsonPath("$.dataVencimento").value("2026-07-10"))
                .andExpect(jsonPath("$.numeroFatura").value("FAT-2026-06"))
                .andExpect(jsonPath("$.observacoes").value("Conta referente ao consumo de junho."));

        verify(registrarContaMensalUseCase).executar(any());
    }

    @Test
    void deveListarContasMensaisComSucesso() throws Exception {
        List<ContaMensalResponse> response = List.of(
                new ContaMensalResponse(
                        1L,
                        YearMonth.of(2026, 6),
                        bd("10000.00"),
                        bd("1200.00"),
                        LocalDate.of(2026, 7, 10),
                        "FAT-2026-06",
                        "Conta referente ao consumo de junho."
                ),
                new ContaMensalResponse(
                        2L,
                        YearMonth.of(2026, 5),
                        bd("8500.00"),
                        bd("1000.00"),
                        LocalDate.of(2026, 6, 10),
                        "FAT-2026-05",
                        "Conta referente ao consumo de maio."
                )
        );

        when(listarContasMensaisUseCase.executar())
                .thenReturn(response);

        mockMvc.perform(get("/contas-mensais"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$[0].valorTotal").value(10000.00))
                .andExpect(jsonPath("$[0].consumoInformado").value(1200.00))
                .andExpect(jsonPath("$[0].dataVencimento").value("2026-07-10"))
                .andExpect(jsonPath("$[0].numeroFatura").value("FAT-2026-06"))
                .andExpect(jsonPath("$[0].observacoes").value("Conta referente ao consumo de junho."))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].mesReferencia").value("2026-05"))
                .andExpect(jsonPath("$[1].valorTotal").value(8500.00))
                .andExpect(jsonPath("$[1].consumoInformado").value(1000.00))
                .andExpect(jsonPath("$[1].dataVencimento").value("2026-06-10"))
                .andExpect(jsonPath("$[1].numeroFatura").value("FAT-2026-05"))
                .andExpect(jsonPath("$[1].observacoes").value("Conta referente ao consumo de maio."));

        verify(listarContasMensaisUseCase).executar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremContasMensais() throws Exception {
        when(listarContasMensaisUseCase.executar())
                .thenReturn(List.of());

        mockMvc.perform(get("/contas-mensais"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarContasMensaisUseCase).executar();
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }
}