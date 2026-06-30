package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import br.com.armandorodrigues.gasrateio.domain.model.TipoMedidor;
import jakarta.persistence.*;

@Entity
@Table(
        name = "medidores",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_medidor_codigo", columnNames = "codigo")
        }
)
public class MedidorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoMedidor tipo;

    @Column(name = "torre_id")
    private Long torreId;

    @Column(nullable = false)
    private boolean ativo;

    protected MedidorEntity() {
    }

    public MedidorEntity(
            Long id,
            String nome,
            String codigo,
            TipoMedidor tipo,
            Long torreId,
            boolean ativo
    ) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
        this.tipo = tipo;
        this.torreId = torreId;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCodigo() {
        return codigo;
    }

    public TipoMedidor getTipo() {
        return tipo;
    }

    public Long getTorreId() {
        return torreId;
    }

    public boolean isAtivo() {
        return ativo;
    }
}