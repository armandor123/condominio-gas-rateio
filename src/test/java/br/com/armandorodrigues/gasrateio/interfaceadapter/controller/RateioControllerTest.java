package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.ItemRateioResponse;
import br.com.armandorodrigues.gasrateio.application.dto.RateioResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.BuscarRateioPorMesUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.CalcularRateioUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarRateiosUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RateioController.class)
class RateioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalcularRateioUseCase calcularRateioUseCase;

    @MockitoBean
    private ListarRateiosUseCase listarRateiosUseCase;

    @MockitoBean
    private BuscarRateioPorMesUseCase buscarRateioPorMesUseCase;

    @Test
    void deveCalcularRateioComSucesso() throws Exception {
        RateioResponse response = criarRateioResponse(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1000.00"),
                bd("1200.00"),
                bd("200.00")
        );

        when(calcularRateioUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/rateios/calcular")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "mesReferencia": "2026-06"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$.contaMensalId").value(1))
                .andExpect(jsonPath("$.valorTotalConta").value(10000.00))
                .andExpect(jsonPath("$.consumoTotalSecundario").value(1000.00))
                .andExpect(jsonPath("$.consumoMedidorPrincipal").value(1200.00))
                .andExpect(jsonPath("$.diferencaConsumo").value(200.00))
                .andExpect(jsonPath("$.itens", hasSize(2)))

                .andExpect(jsonPath("$.itens[0].torreId").value(1))
                .andExpect(jsonPath("$.itens[0].nomeTorre").value("Prime"))
                .andExpect(jsonPath("$.itens[0].medidorId").value(2))
                .andExpect(jsonPath("$.itens[0].consumo").value(600.00))
                .andExpect(jsonPath("$.itens[0].percentual").value(60.00))
                .andExpect(jsonPath("$.itens[0].valorRateado").value(6000.00))

                .andExpect(jsonPath("$.itens[1].torreId").value(2))
                .andExpect(jsonPath("$.itens[1].nomeTorre").value("Hype"))
                .andExpect(jsonPath("$.itens[1].medidorId").value(3))
                .andExpect(jsonPath("$.itens[1].consumo").value(400.00))
                .andExpect(jsonPath("$.itens[1].percentual").value(40.00))
                .andExpect(jsonPath("$.itens[1].valorRateado").value(4000.00));

        verify(calcularRateioUseCase).executar(any());
    }

    @Test
    void deveListarRateiosComSucesso() throws Exception {
        List<RateioResponse> response = List.of(
                criarRateioResponse(
                        1L,
                        YearMonth.of(2026, 6),
                        bd("10000.00"),
                        bd("1000.00"),
                        bd("1200.00"),
                        bd("200.00")
                ),
                criarRateioResponse(
                        2L,
                        YearMonth.of(2026, 5),
                        bd("8500.00"),
                        bd("850.00"),
                        bd("1000.00"),
                        bd("150.00")
                )
        );

        when(listarRateiosUseCase.executar())
                .thenReturn(response);

        mockMvc.perform(get("/rateios"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$[0].valorTotalConta").value(10000.00))
                .andExpect(jsonPath("$[0].consumoTotalSecundario").value(1000.00))
                .andExpect(jsonPath("$[0].consumoMedidorPrincipal").value(1200.00))
                .andExpect(jsonPath("$[0].diferencaConsumo").value(200.00))
                .andExpect(jsonPath("$[0].itens", hasSize(2)))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].mesReferencia").value("2026-05"))
                .andExpect(jsonPath("$[1].valorTotalConta").value(8500.00))
                .andExpect(jsonPath("$[1].consumoTotalSecundario").value(850.00))
                .andExpect(jsonPath("$[1].consumoMedidorPrincipal").value(1000.00))
                .andExpect(jsonPath("$[1].diferencaConsumo").value(150.00))
                .andExpect(jsonPath("$[1].itens", hasSize(2)));

        verify(listarRateiosUseCase).executar();
    }

    @Test
    void deveBuscarRateioPorMesComSucesso() throws Exception {
        RateioResponse response = criarRateioResponse(
                1L,
                YearMonth.of(2026, 6),
                bd("10000.00"),
                bd("1000.00"),
                bd("1200.00"),
                bd("200.00")
        );

        when(buscarRateioPorMesUseCase.executar("2026-06"))
                .thenReturn(response);

        mockMvc.perform(get("/rateios/2026-06"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.mesReferencia").value("2026-06"))
                .andExpect(jsonPath("$.valorTotalConta").value(10000.00))
                .andExpect(jsonPath("$.consumoTotalSecundario").value(1000.00))
                .andExpect(jsonPath("$.consumoMedidorPrincipal").value(1200.00))
                .andExpect(jsonPath("$.diferencaConsumo").value(200.00))
                .andExpect(jsonPath("$.itens", hasSize(2)));

        verify(buscarRateioPorMesUseCase).executar("2026-06");
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremRateios() throws Exception {
        when(listarRateiosUseCase.executar())
                .thenReturn(List.of());

        mockMvc.perform(get("/rateios"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarRateiosUseCase).executar();
    }

    private RateioResponse criarRateioResponse(
            Long id,
            YearMonth mesReferencia,
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            BigDecimal consumoMedidorPrincipal,
            BigDecimal diferencaConsumo
    ) {
        List<ItemRateioResponse> itens = List.of(
                new ItemRateioResponse(
                        1L,
                        1L,
                        "Prime",
                        2L,
                        bd("600.00"),
                        bd("60.00"),
                        bd("6000.00")
                ),
                new ItemRateioResponse(
                        2L,
                        2L,
                        "Hype",
                        3L,
                        bd("400.00"),
                        bd("40.00"),
                        bd("4000.00")
                )
        );

        return new RateioResponse(
                id,
                mesReferencia,
                1L,
                valorTotalConta,
                consumoTotalSecundario,
                consumoMedidorPrincipal,
                diferencaConsumo,
                LocalDateTime.of(2026, mesReferencia.getMonthValue(), 28, 10, 0),
                itens
        );
    }

    private BigDecimal bd(String valor) {
        return new BigDecimal(valor);
    }
}