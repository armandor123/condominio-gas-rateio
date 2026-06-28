package br.com.armandorodrigues.gasrateio.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "torres",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_torre_nome", columnNames = "nome")
        }
)
public class TorreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private boolean ativa;

    protected TorreEntity() {
    }

    public TorreEntity(Long id, String nome, boolean ativa) {
        this.id = id;
        this.nome = nome;
        this.ativa = ativa;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public boolean isAtiva() {
        return ativa;
    }
}
