package br.com.armandorodrigues.gasrateio.interfaceadapter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "Sistema de Rateio de Gás está rodando!";
    }
}
