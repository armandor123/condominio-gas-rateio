package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.MedidorResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.CadastrarMedidorUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarMedidoresUseCase;
import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MedidorController.class)
class MedidorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CadastrarMedidorUseCase cadastrarMedidorUseCase;

    @MockitoBean
    private ListarMedidoresUseCase listarMedidoresUseCase;

    @Test
    void deveCadastrarMedidorPrincipalComSucesso() throws Exception {
        MedidorResponse response = new MedidorResponse(
                1L,
                "Medidor Principal",
                "GAS-PRINCIPAL-001",
                TipoMedidor.PRINCIPAL,
                null,
                true
        );

        when(cadastrarMedidorUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/medidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Medidor Principal",
                                  "codigo": "GAS-PRINCIPAL-001",
                                  "tipo": "PRINCIPAL",
                                  "torreId": null
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Medidor Principal"))
                .andExpect(jsonPath("$.codigo").value("GAS-PRINCIPAL-001"))
                .andExpect(jsonPath("$.tipo").value("PRINCIPAL"))
                .andExpect(jsonPath("$.torreId").isEmpty())
                .andExpect(jsonPath("$.ativo").value(true));

        verify(cadastrarMedidorUseCase).executar(any());
    }

    @Test
    void deveCadastrarMedidorSecundarioComSucesso() throws Exception {
        MedidorResponse response = new MedidorResponse(
                2L,
                "Medidor Torre Prime",
                "GAS-PRIME-001",
                TipoMedidor.SECUNDARIO,
                1L,
                true
        );

        when(cadastrarMedidorUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/medidores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Medidor Torre Prime",
                                  "codigo": "GAS-PRIME-001",
                                  "tipo": "SECUNDARIO",
                                  "torreId": 1
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.nome").value("Medidor Torre Prime"))
                .andExpect(jsonPath("$.codigo").value("GAS-PRIME-001"))
                .andExpect(jsonPath("$.tipo").value("SECUNDARIO"))
                .andExpect(jsonPath("$.torreId").value(1))
                .andExpect(jsonPath("$.ativo").value(true));

        verify(cadastrarMedidorUseCase).executar(any());
    }

    @Test
    void deveListarMedidoresComSucesso() throws Exception {
        List<MedidorResponse> response = List.of(
                new MedidorResponse(
                        1L,
                        "Medidor Principal",
                        "GAS-PRINCIPAL-001",
                        TipoMedidor.PRINCIPAL,
                        null,
                        true
                ),
                new MedidorResponse(
                        2L,
                        "Medidor Torre Prime",
                        "GAS-PRIME-001",
                        TipoMedidor.SECUNDARIO,
                        1L,
                        true
                ),
                new MedidorResponse(
                        3L,
                        "Medidor Torre Hype",
                        "GAS-HYPE-001",
                        TipoMedidor.SECUNDARIO,
                        2L,
                        true
                )
        );

        when(listarMedidoresUseCase.executar())
                .thenReturn(response);

        mockMvc.perform(get("/medidores"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))

                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Medidor Principal"))
                .andExpect(jsonPath("$[0].codigo").value("GAS-PRINCIPAL-001"))
                .andExpect(jsonPath("$[0].tipo").value("PRINCIPAL"))
                .andExpect(jsonPath("$[0].torreId").isEmpty())
                .andExpect(jsonPath("$[0].ativo").value(true))

                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Medidor Torre Prime"))
                .andExpect(jsonPath("$[1].codigo").value("GAS-PRIME-001"))
                .andExpect(jsonPath("$[1].tipo").value("SECUNDARIO"))
                .andExpect(jsonPath("$[1].torreId").value(1))
                .andExpect(jsonPath("$[1].ativo").value(true))

                .andExpect(jsonPath("$[2].id").value(3))
                .andExpect(jsonPath("$[2].nome").value("Medidor Torre Hype"))
                .andExpect(jsonPath("$[2].codigo").value("GAS-HYPE-001"))
                .andExpect(jsonPath("$[2].tipo").value("SECUNDARIO"))
                .andExpect(jsonPath("$[2].torreId").value(2))
                .andExpect(jsonPath("$[2].ativo").value(true));

        verify(listarMedidoresUseCase).executar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremMedidores() throws Exception {
        when(listarMedidoresUseCase.executar())
                .thenReturn(List.of());

        mockMvc.perform(get("/medidores"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarMedidoresUseCase).executar();
    }
}
