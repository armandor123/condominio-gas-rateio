package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

public class Rateio {

    private Long id;
    private YearMonth mesReferencia;
    private Long contaMensalId;
    private BigDecimal valorTotalConta;
    private BigDecimal consumoTotalSecundario;
    private BigDecimal consumoMedidorPrincipal;
    private BigDecimal diferencaConsumo;
    private LocalDateTime dataCalculo;
    private List<ItemRateio> itens;

    public Rateio(
            Long id,
            YearMonth mesReferencia,
            Long contaMensalId,
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            BigDecimal consumoMedidorPrincipal,
            BigDecimal diferencaConsumo,
            LocalDateTime dataCalculo,
            List<ItemRateio> itens
    ) {
        validarMesReferencia(mesReferencia);
        validarContaMensal(contaMensalId);
        validarValorTotalConta(valorTotalConta);
        validarConsumoTotalSecundario(consumoTotalSecundario);
        validarConsumoMedidorPrincipal(consumoMedidorPrincipal);
        validarDiferencaConsumo(diferencaConsumo);
        validarDataCalculo(dataCalculo);
        validarItens(itens);

        this.id = id;
        this.mesReferencia = mesReferencia;
        this.contaMensalId = contaMensalId;
        this.valorTotalConta = valorTotalConta;
        this.consumoTotalSecundario = consumoTotalSecundario;
        this.consumoMedidorPrincipal = consumoMedidorPrincipal;
        this.diferencaConsumo = diferencaConsumo;
        this.dataCalculo = dataCalculo;
        this.itens = List.copyOf(itens);
    }

    public static Rateio novo(
            YearMonth mesReferencia,
            Long contaMensalId,
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            BigDecimal consumoMedidorPrincipal,
            BigDecimal diferencaConsumo,
            List<ItemRateio> itens
    ) {
        return new Rateio(
                null,
                mesReferencia,
                contaMensalId,
                valorTotalConta,
                consumoTotalSecundario,
                consumoMedidorPrincipal,
                diferencaConsumo,
                LocalDateTime.now(),
                itens
        );
    }

    private void validarMesReferencia(YearMonth mesReferencia) {
        if (mesReferencia == null) {
            throw new RegraNegocioException("O mês de referência do rateio é obrigatório.");
        }
    }

    private void validarContaMensal(Long contaMensalId) {
        if (contaMensalId == null || contaMensalId <= 0) {
            throw new RegraNegocioException("A conta mensal do rateio é obrigatória.");
        }
    }

    private void validarValorTotalConta(BigDecimal valorTotalConta) {
        if (valorTotalConta == null) {
            throw new RegraNegocioException("O valor total da conta é obrigatório para o rateio.");
        }

        if (valorTotalConta.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O valor total da conta deve ser maior que zero para o rateio.");
        }
    }

    private void validarConsumoTotalSecundario(BigDecimal consumoTotalSecundario) {
        if (consumoTotalSecundario == null) {
            throw new RegraNegocioException("O consumo total dos medidores secundários é obrigatório.");
        }

        if (consumoTotalSecundario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("O consumo total dos medidores secundários deve ser maior que zero.");
        }
    }

    private void validarConsumoMedidorPrincipal(BigDecimal consumoMedidorPrincipal) {
        if (consumoMedidorPrincipal == null) {
            throw new RegraNegocioException("O consumo do medidor principal é obrigatório.");
        }

        if (consumoMedidorPrincipal.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O consumo do medidor principal não pode ser negativo.");
        }
    }

    private void validarDiferencaConsumo(BigDecimal diferencaConsumo) {
        if (diferencaConsumo == null) {
            throw new RegraNegocioException("A diferença de consumo é obrigatória.");
        }
    }

    private void validarDataCalculo(LocalDateTime dataCalculo) {
        if (dataCalculo == null) {
            throw new RegraNegocioException("A data de cálculo do rateio é obrigatória.");
        }
    }

    private void validarItens(List<ItemRateio> itens) {
        if (itens == null || itens.isEmpty()) {
            throw new RegraNegocioException("O rateio precisa ter pelo menos um item.");
        }
    }

    public Long getId() {
        return id;
    }

    public YearMonth getMesReferencia() {
        return mesReferencia;
    }

    public Long getContaMensalId() {
        return contaMensalId;
    }

    public BigDecimal getValorTotalConta() {
        return valorTotalConta;
    }

    public BigDecimal getConsumoTotalSecundario() {
        return consumoTotalSecundario;
    }

    public BigDecimal getConsumoMedidorPrincipal() {
        return consumoMedidorPrincipal;
    }

    public BigDecimal getDiferencaConsumo() {
        return diferencaConsumo;
    }

    public LocalDateTime getDataCalculo() {
        return dataCalculo;
    }

    public List<ItemRateio> getItens() {
        return itens;
    }
}