package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_rateio")
public class ItemRateioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rateio_id", nullable = false)
    private RateioEntity rateio;

    @Column(name = "torre_id", nullable = false)
    private Long torreId;

    @Column(name = "nome_torre", nullable = false, length = 100)
    private String nomeTorre;

    @Column(name = "medidor_id", nullable = false)
    private Long medidorId;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal consumo;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal percentual;

    @Column(name = "valor_rateado", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorRateado;

    protected ItemRateioEntity() {
    }

    public ItemRateioEntity(
            Long id,
            Long torreId,
            String nomeTorre,
            Long medidorId,
            BigDecimal consumo,
            BigDecimal percentual,
            BigDecimal valorRateado
    ) {
        this.id = id;
        this.torreId = torreId;
        this.nomeTorre = nomeTorre;
        this.medidorId = medidorId;
        this.consumo = consumo;
        this.percentual = percentual;
        this.valorRateado = valorRateado;
    }

    public void setRateio(RateioEntity rateio) {
        this.rateio = rateio;
    }

    public Long getId() {
        return id;
    }

    public RateioEntity getRateio() {
        return rateio;
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