package br.com.armandorodrigues.gasrateio.domain.model;

import br.com.armandorodrigues.gasrateio.domain.exception.RegraNegocioException;

public class Medidor {

    private Long id;
    private String nome;
    private String codigo;
    private TipoMedidor tipo;
    private Long torreId;
    private boolean ativo;

    public Medidor(
            Long id,
            String nome,
            String codigo,
            TipoMedidor tipo,
            Long torreId,
            boolean ativo
    ) {
        validarNome(nome);
        validarCodigo(codigo);
        validarTipo(tipo);
        validarVinculoComTorre(tipo, torreId);

        this.id = id;
        this.nome = nome.trim();
        this.codigo = codigo.trim();
        this.tipo = tipo;
        this.torreId = torreId;
        this.ativo = ativo;
    }

    public static Medidor novoPrincipal(String nome, String codigo) {
        return new Medidor(
                null,
                nome,
                codigo,
                TipoMedidor.PRINCIPAL,
                null,
                true
        );
    }

    public static Medidor novoSecundario(String nome, String codigo, Long torreId) {
        return new Medidor(
                null,
                nome,
                codigo,
                TipoMedidor.SECUNDARIO,
                torreId,
                true
        );
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new RegraNegocioException("O nome do medidor é obrigatório.");
        }

        if (nome.trim().length() < 2) {
            throw new RegraNegocioException("O nome do medidor deve ter pelo menos 2 caracteres.");
        }
    }

    private void validarCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new RegraNegocioException("O código do medidor é obrigatório.");
        }

        if (codigo.trim().length() < 2) {
            throw new RegraNegocioException("O código do medidor deve ter pelo menos 2 caracteres.");
        }
    }

    private void validarTipo(TipoMedidor tipo) {
        if (tipo == null) {
            throw new RegraNegocioException("O tipo do medidor é obrigatório.");
        }
    }

    private void validarVinculoComTorre(TipoMedidor tipo, Long torreId) {
        if (tipo == TipoMedidor.PRINCIPAL && torreId != null) {
            throw new RegraNegocioException("Medidor principal não deve estar vinculado a uma torre.");
        }

        if (tipo == TipoMedidor.SECUNDARIO && torreId == null) {
            throw new RegraNegocioException("Medidor secundário deve estar vinculado a uma torre.");
        }

        if (torreId != null && torreId <= 0) {
            throw new RegraNegocioException("O ID da torre deve ser válido.");
        }
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