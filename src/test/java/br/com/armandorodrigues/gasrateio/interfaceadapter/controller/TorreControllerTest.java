package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import br.com.armandorodrigues.gasrateio.application.dto.TorreResponse;
import br.com.armandorodrigues.gasrateio.application.usecase.CadastrarTorreUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarTorresUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TorreController.class)
class TorreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CadastrarTorreUseCase cadastrarTorreUseCase;

    @MockitoBean
    private ListarTorresUseCase listarTorresUseCase;

    @Test
    void deveCadastrarTorreComSucesso() throws Exception {
        TorreResponse response = new TorreResponse(
                1L,
                "Prime",
                true
        );

        when(cadastrarTorreUseCase.executar(any()))
                .thenReturn(response);

        mockMvc.perform(post("/torres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Prime"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Prime"))
                .andExpect(jsonPath("$.ativa").value(true));

        verify(cadastrarTorreUseCase).executar(any());
    }

    @Test
    void deveListarTorresComSucesso() throws Exception {
        List<TorreResponse> response = List.of(
                new TorreResponse(
                        1L,
                        "Prime",
                        true
                ),
                new TorreResponse(
                        2L,
                        "Hype",
                        true
                )
        );

        when(listarTorresUseCase.executar())
                .thenReturn(response);

        mockMvc.perform(get("/torres"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].nome").value("Prime"))
                .andExpect(jsonPath("$[0].ativa").value(true))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].nome").value("Hype"))
                .andExpect(jsonPath("$[1].ativa").value(true));

        verify(listarTorresUseCase).executar();
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoExistiremTorres() throws Exception {
        when(listarTorresUseCase.executar())
                .thenReturn(List.of());

        mockMvc.perform(get("/torres"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));

        verify(listarTorresUseCase).executar();
    }
}