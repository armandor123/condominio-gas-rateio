package br.com.armandorodrigues.gasrateio.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI gasRateioOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Rateio de Gás Condominial")
                        .description("API para cadastro de torres, medidores, leituras, contas mensais e cálculo de rateio de gás entre torres.")
                        .version("1.0.0"));
    }
}