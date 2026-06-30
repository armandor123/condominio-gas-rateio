package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

public class ContaMensal {

    private Long id;
    private YearMonth mesReferencia;
    private BigDecimal valorTotal;
    private BigDecimal consumoInformado;
    private LocalDate dataVencimento;
    private String numeroFatura;
    private String observacoes;

    public ContaMensal(
            Long id,
            YearMonth mesReferencia,
            BigDecimal valorTotal,
            BigDecimal consumoInformado,
            LocalDate dataVencimento,
            String numeroFatura,
            String observacoes
    ) {
        validarMesReferencia(mesReferencia);
        validarValorTotal(valorTotal);
        validarConsumoInformado(consumoInformado);
        validarDataVencimento(dataVencimento);

        this.id = id;
        this.mesReferencia = mesReferencia;
        this.valorTotal = valorTotal;
        this.consumoInformado = consumoInformado;
        this.dataVencimento = dataVencimento;
        this.numeroFatura = normalizarTextoOpcional(numeroFatura);
        this.observacoes = normalizarTextoOpcional(observacoes);
    }

    public static ContaMensal nova(
            YearMonth mesReferencia,
            BigDecimal valorTotal,
            BigDecimal consumoInformado,
            LocalDate dataVencimento,
            String numeroFatura,
            String observacoes
    ) {
        return new ContaMensal(
                null,
                mesReferencia,
                valorTotal,
                consumoInformado,
                dataVencimento,
                numeroFatura,
                observacoes
        );
    }

    private void validarMesReferencia(YearMonth mesReferencia) {
        if (mesReferencia == null) {
            throw new RegraNegocioException("O mês de referência é obrigatório.");
        }
    }

    private void validarValorTotal(BigDecimal valorTotal) {
        if (valorTotal == null) {
            throw new RegraNegocioException("O valor total da conta é obrigatório.");
        }

        if (valorTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor total da conta deve ser maior que zero.");
        }
    }

    private void validarConsumoInformado(BigDecimal consumoInformado) {
        if (consumoInformado == null) {
            throw new RegraNegocioException("O consumo informado é obrigatório.");
        }

        if (consumoInformado.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O consumo informado não pode ser negativo.");
        }
    }

    private void validarDataVencimento(LocalDate dataVencimento) {
        if (dataVencimento == null) {
            throw new RegraNegocioException("A data de vencimento é obrigatória.");
        }
    }

    private String normalizarTextoOpcional(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return null;
        }

        return texto.trim();
    }

    public Long getId() {
        return id;
    }

    public YearMonth getMesReferencia() {
        return mesReferencia;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getConsumoInformado() {
        return consumoInformado;
    }

    public LocalDate getDataVencimento() {
        return dataVencimento;
    }

    public String getNumeroFatura() {
        return numeroFatura;
    }

    public String getObservacoes() {
        return observacoes;
    }
}