package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

import java.math.BigDecimal;

public class ItemRateio {

    private Long id;
    private Long torreId;
    private String nomeTorre;
    private Long medidorId;
    private BigDecimal consumo;
    private BigDecimal percentual;
    private BigDecimal valorRateado;

    public ItemRateio(
            Long id,
            Long torreId,
            String nomeTorre,
            Long medidorId,
            BigDecimal consumo,
            BigDecimal percentual,
            BigDecimal valorRateado
    ) {
        validarTorre(torreId, nomeTorre);
        validarMedidor(medidorId);
        validarConsumo(consumo);
        validarPercentual(percentual);
        validarValorRateado(valorRateado);

        this.id = id;
        this.torreId = torreId;
        this.nomeTorre = nomeTorre.trim();
        this.medidorId = medidorId;
        this.consumo = consumo;
        this.percentual = percentual;
        this.valorRateado = valorRateado;
    }

    public static ItemRateio novo(
            Long torreId,
            String nomeTorre,
            Long medidorId,
            BigDecimal consumo,
            BigDecimal percentual,
            BigDecimal valorRateado
    ) {
        return new ItemRateio(
                null,
                torreId,
                nomeTorre,
                medidorId,
                consumo,
                percentual,
                valorRateado
        );
    }

    private void validarTorre(Long torreId, String nomeTorre) {
        if (torreId == null || torreId <= 0) {
            throw new RegraNegocioException("A torre do item de rateio é obrigatória.");
        }

        if (nomeTorre == null || nomeTorre.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da torre do item de rateio é obrigatório.");
        }
    }

    private void validarMedidor(Long medidorId) {
        if (medidorId == null || medidorId <= 0) {
            throw new RegraNegocioException("O medidor do item de rateio é obrigatório.");
        }
    }

    private void validarConsumo(BigDecimal consumo) {
        if (consumo == null) {
            throw new RegraNegocioException("O consumo do item de rateio é obrigatório.");
        }

        if (consumo.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O consumo do item de rateio não pode ser negativo.");
        }
    }

    private void validarPercentual(BigDecimal percentual) {
        if (percentual == null) {
            throw new RegraNegocioException("O percentual do item de rateio é obrigatório.");
        }

        if (percentual.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O percentual do item de rateio não pode ser negativo.");
        }
    }

    private void validarValorRateado(BigDecimal valorRateado) {
        if (valorRateado == null) {
            throw new RegraNegocioException("O valor rateado é obrigatório.");
        }

        if (valorRateado.compareTo(BigDecimal.ZERO) < 0) {
            throw new RegraNegocioException("O valor rateado não pode ser negativo.");
        }
    }

    public Long getId() {
        return id;
    }

    public Long getTorreId() {
        return torreId;
    }

    public String getNomeTorre() {
        return nomeTorre;
    }

    public Long getMedidorId() {
        return medidorId;
    }

    public BigDecimal getConsumo() {
        return consumo;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public BigDecimal getValorRateado() {
        return valorRateado;
    }
}