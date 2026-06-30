package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "rateios",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_rateio_mes_referencia",
                        columnNames = "mes_referencia"
                )
        }
)
public class RateioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "conta_mensal_id", nullable = false)
    private Long contaMensalId;

    @Column(name = "valor_total_conta", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotalConta;

    @Column(name = "consumo_total_secundario", nullable = false, precision = 12, scale = 3)
    private BigDecimal consumoTotalSecundario;

    @Column(name = "consumo_medidor_principal", nullable = false, precision = 12, scale = 3)
    private BigDecimal consumoMedidorPrincipal;

    @Column(name = "diferenca_consumo", nullable = false, precision = 12, scale = 3)
    private BigDecimal diferencaConsumo;

    @Column(name = "data_calculo", nullable = false)
    private LocalDateTime dataCalculo;

    @OneToMany(
            mappedBy = "rateio",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<ItemRateioEntity> itens = new ArrayList<>();

    protected RateioEntity() {
    }

    public RateioEntity(
            Long id,
            String mesReferencia,
            Long contaMensalId,
            BigDecimal valorTotalConta,
            BigDecimal consumoTotalSecundario,
            BigDecimal consumoMedidorPrincipal,
            BigDecimal diferencaConsumo,
            LocalDateTime dataCalculo
    ) {
        this.id = id;
        this.mesReferencia = mesReferencia;
        this.contaMensalId = contaMensalId;
        this.valorTotalConta = valorTotalConta;
        this.consumoTotalSecundario = consumoTotalSecundario;
        this.consumoMedidorPrincipal = consumoMedidorPrincipal;
        this.diferencaConsumo = diferencaConsumo;
        this.dataCalculo = dataCalculo;
    }

    public void adicionarItem(ItemRateioEntity item) {
        itens.add(item);
        item.setRateio(this);
    }

    public Long getId() {
        return id;
    }

    public String getMesReferencia() {
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

    public List<ItemRateioEntity> getItens() {
        return itens;
    }
}