package br.com.armandorodrigues.gasrateio.interfaceadapter.exception;

import br.com.armandorodrigues.gasrateio.application.usecase.CadastrarTorreUseCase;
import br.com.armandorodrigues.gasrateio.application.usecase.ListarTorresUseCase;
import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;
import br.com.armandorodrigues.gasrateio.interfaceadapter.controller.TorreController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TorreController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CadastrarTorreUseCase cadastrarTorreUseCase;

    @MockitoBean
    private ListarTorresUseCase listarTorresUseCase;

    @Test
    void deveRetornarBadRequestQuandoOcorrerRegraDeNegocioException() throws Exception {
        when(cadastrarTorreUseCase.executar(any()))
                .thenThrow(new RegraNegocioException("Já existe uma torre cadastrada com este nome."));

        mockMvc.perform(post("/torres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Prime"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(cadastrarTorreUseCase).executar(any());
    }
}