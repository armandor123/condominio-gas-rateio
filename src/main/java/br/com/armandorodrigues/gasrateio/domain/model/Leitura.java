package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class Leitura {

    private Long id;
    private Long medidorId;
    private YearMonth mesReferencia;
    private LocalDate dataLeitura;
    private BigDecimal leituraAnterior;
    private BigDecimal leituraAtual;
    private BigDecimal consumo;

    public Leitura(
            Long id,
            Long medidorId,
            YearMonth mesReferencia,
            LocalDate dataLeitura,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual
    ) {
        validarMedidor(medidorId);
        validarMesReferencia(mesReferencia);
        validarDataLeitura(dataLeitura);
        validarLeituras(leituraAnterior, leituraAtual);

        this.id = id;
        this.medidorId = medidorId;
        this.mesReferencia = mesReferencia;
        this.dataLeitura = dataLeitura;
        this.leituraAnterior = leituraAnterior;
        this.leituraAtual = leituraAtual;
        this.consumo = calcularConsumo(leituraAnterior, leituraAtual);
    }

    public static Leitura nova(
            Long medidorId,
            YearMonth mesReferencia,
            LocalDate dataLeitura,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual
    ) {
        return new Leitura(
                null,
                medidorId,
                mesReferencia,
                dataLeitura,
                leituraAnterior,
                leituraAtual
        );
    }

    private void validarMedidor(Long medidorId) {
        if (medidorId == null || medidorId <= 0) {
            throw new RegraNegocioException("O medidor é obrigatório.");
        }
    }

    private void validarMesReferencia(YearMonth mesReferencia) {
        if (mesReferencia == null) {
            throw new RegraNegocioException("O mês de referência é obrigatório.");
        }
    }

    private void validarDataLeitura(LocalDate dataLeitura) {
        if (dataLeitura == null) {
            throw new RegraNegocioException("A data da leitura é obrigatória.");
        }
    }

    private void validarLeituras(BigDecimal leituraAnterior, BigDecimal leituraAtual) {
        if (leituraAnterior == null) {
            throw new RegraNegocioException("A leitura anterior é obrigatória.");
        }

        if (leituraAtual == null) {
            throw new RegraNegocioException("A leitura atual é obrigatória.");
        }

        if (leituraAnterior.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("A leitura anterior não pode ser negativa.");
        }

        if (leituraAtual.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("A leitura atual não pode ser negativa.");
        }

        if (leituraAtual.compareTo(leituraAnterior) < 0) {
            throw new RegraNegocioException("A leitura atual não pode ser menor que a leitura anterior.");
        }
    }

    private BigDecimal calcularConsumo(BigDecimal leituraAnterior, BigDecimal leituraAtual) {
        return leituraAtual.subtract(leituraAnterior);
    }

    public Long getId() {
        return id;
    }

    public Long getMedidorId() {
        return medidorId;
    }

    public YearMonth getMesReferencia() {
        return mesReferencia;
    }

    public LocalDate getDataLeitura() {
        return dataLeitura;
    }

    public BigDecimal getLeituraAnterior() {
        return leituraAnterior;
    }

    public BigDecimal getLeituraAtual() {
        return leituraAtual;
    }

    public BigDecimal getConsumo() {
        return consumo;
    }
}