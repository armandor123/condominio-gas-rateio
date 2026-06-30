package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        name = "contas_mensais",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conta_mensal_mes_referencia",
                        columnNames = "mes_referencia"
                )
        }
)
public class ContaMensalEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_referencia", nullable = false, length = 7)
    private String mesReferencia;

    @Column(name = "valor_total", nullable = false, precision = 14, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "consumo_informado", nullable = false, precision = 12, scale = 3)
    private BigDecimal consumoInformado;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "numero_fatura", length = 50)
    private String numeroFatura;

    @Column(length = 500)
    private String observacoes;

    protected ContaMensalEntity() {
    }

    public ContaMensalEntity(
            Long id,
            String mesReferencia,
            BigDecimal valorTotal,
            BigDecimal consumoInformado,
            LocalDate dataVencimento,
            String numeroFatura,
            String observacoes
    ) {
        this.id = id;
        this.mesReferencia = mesReferencia;
        this.valorTotal = valorTotal;
        this.consumoInformado = consumoInformado;
        this.dataVencimento = dataVencimento;
        this.numeroFatura = numeroFatura;
        this.observacoes = observacoes;
    }

    public Long getId() {
        return id;
    }

    public String getMesReferencia() {
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