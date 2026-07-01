package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.LeituraResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarLeiturasUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.RegistrarLeituraUseCase;
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

@WebMvcTest(LeituraController.class)
class LeituraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RegistrarLeituraUseCase registrarLeituraUseCase;

    @MockitoBean
    private ListarLeiturasUseCase listarLeiturasUseCase;

    @Test
    void deveRegistrarLeituraComSucesso() throws Exception {
        LeituraResponse response = new LeituraResponse(
                1L,
                1L,
                YearMonth.of(2026, 6),
                LocalDate.of(2026, 6, 30),
                bd("10000.00"),
                bd("11200.00"),
                bd("1200.00")
        );

        when(registrarLeituraUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/leituras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "medidorId": 1,
                                  "mesReferencia": "2026-06",
                                  "dataLeitura": "2026-06-30",
                                  "leituraAnterior": 10000.00,
                                  "leituraAtual": 11200.00
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.medidorId").value(1))
                .andExpect(jsonPath("$.mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$.dataLeitura").value("2026-06-30"))
                .andExpect(jsonPath("$.leituraAnterior").value(10000.00))
                .andExpect(jsonPath("$.leituraAtual").value(11200.00))
                .andExpect(jsonPath("$.consumo").value(1200.00));

        verify(registrarLeituraUseCase).executar(any());
    }

    @Test
    void deveListarLeiturasComSucesso() throws Exception {
        List<LeituraResponse> response = List.of(
                new LeituraResponse(
                        1L,
                        1L,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("10000.00"),
                        bd("11200.00"),
                        bd("1200.00")
                ),
                new LeituraResponse(
                        2L,
                        2L,
                        YearMonth.of(2026, 6),
                        LocalDate.of(2026, 6, 30),
                        bd("5000.00"),
                        bd("5600.00"),
                        bd("600.00")
                )
        );

        when(listarLeiturasUseCase.executar())
                .thenReturn(response);

        mockMvc.perform(get("/leituras"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].medidorId").value(1))
                .andExpect(jsonPath("$[0].mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$[0].dataLeitura").value("2026-06-30"))
                .andExpect(jsonPath("$[0].leituraAnterior").value(10000.00))
                .andExpect(jsonPath("$[0].leituraAtual").value(11200.00))
                .andExpect(jsonPath("$[0].consumo").value(1200.00))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].medidorId").value(2))
                .andExpect(jsonPath("$[1].mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$[1].dataLeitura").value("2026-06-30"))
                .andExpect(jsonPath("$[1].leituraAnterior").value(5000.00))
                .andExpect(jsonPath("$[1].leituraAtual").value(5600.00))
                .andExpect(jsonPath("$[1].consumo").value(600.00));

        verify(listarLeiturasUseCase).executar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremLeituras() throws Exception {
        when(listarLeiturasUseCase.executar())
                .thenReturn(List.of());

        mockMvc.perform(get("/leituras"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarLeiturasUseCase).executar();
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }
}