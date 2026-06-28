package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

public class Torre {

    private Long id;
    private String nome;
    private boolean ativa;

    public Torre(Long id, String nome, boolean ativa) {
        validarNome(nome);
        this.id = id;
        this.nome = nome.trim();
        this.ativa = ativa;
    }

    public static Torre nova(String nome) {
        return new Torre(null, nome, true);
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome da torre é obrigatório.");
        }

        if (nome.trim().length() < 2) {
            throw new RegraNegocioException("O nome da torre deve ter pelo menos 2 caracteres.");
        }
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
