package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "leituras",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_leitura_medidor_mes",
                        columnNames = {"medidor_id", "mes_referencia"}
                )
        }
)
public class LeituraEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "medidor_id", nullable = false)
    private Long medidorId;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "data_leitura", nullable = false)
    private LocalDate dataLeitura;

    @Column(name = "leitura_anterior", nullable = false, precision = 12, scale = 3)
    private BigDecimal leituraAnterior;

    @Column(name = "leitura_atual", nullable = false, precision = 12, scale = 3)
    private BigDecimal leituraAtual;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal consumo;

    protected LeituraEntity() {
    }

    public LeituraEntity(
            Long id,
            Long medidorId,
            String mesReferencia,
            LocalDate dataLeitura,
            BigDecimal leituraAnterior,
            BigDecimal leituraAtual,
            BigDecimal consumo
    ) {
        this.id = id;
        this.medidorId = medidorId;
        this.mesReferencia = mesReferencia;
        this.dataLeitura = dataLeitura;
        this.leituraAnterior = leituraAnterior;
        this.leituraAtual = leituraAtual;
        this.consumo = consumo;
    }

    public Long getId() {
        return id;
    }

    public Long getMedidorId() {
        return medidorId;
    }

    public String getMesReferencia() {
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