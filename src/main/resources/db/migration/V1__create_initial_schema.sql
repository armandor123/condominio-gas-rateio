CREATE TABLE torres (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE medidores (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    tipo VARCHAR(20) NOT NULL,
    torre_id BIGINT,
    ativo BOOLEAN NOT NULL
);

CREATE TABLE leituras (
    id BIGSERIAL PRIMARY KEY,
    medidor_id BIGINT NOT NULL,
    mes_referencia VARCHAR(7) NOT NULL,
    data_leitura DATE NOT NULL,
    leitura_anterior NUMERIC(12, 3) NOT NULL,
    leitura_atual NUMERIC(12, 3) NOT NULL,
    consumo NUMERIC(12, 3) NOT NULL,
    CONSTRAINT uk_leitura_medidor_mes UNIQUE (medidor_id, mes_referencia)
);

CREATE TABLE contas_mensais (
    id BIGSERIAL PRIMARY KEY,
    mes_referencia VARCHAR(7) NOT NULL,
    valor_total NUMERIC(14, 2) NOT NULL,
    consumo_informado NUMERIC(12, 3) NOT NULL,
    data_vencimento DATE NOT NULL,
    numero_fatura VARCHAR(50),
    observacoes VARCHAR(500),
    CONSTRAINT uk_conta_mensal_mes_referencia UNIQUE (mes_referencia)
);

CREATE TABLE rateios (
    id BIGSERIAL PRIMARY KEY,
    mes_referencia VARCHAR(7) NOT NULL,
    conta_mensal_id BIGINT NOT NULL,
    valor_total_conta NUMERIC(14, 2) NOT NULL,
    consumo_total_secundario NUMERIC(12, 3) NOT NULL,
    consumo_medidor_principal NUMERIC(12, 3) NOT NULL,
    diferenca_consumo NUMERIC(12, 3) NOT NULL,
    data_calculo TIMESTAMP NOT NULL,
    CONSTRAINT uk_rateio_mes_referencia UNIQUE (mes_referencia)
);

CREATE TABLE itens_rateio (
    id BIGSERIAL PRIMARY KEY,
    rateio_id BIGINT NOT NULL,
    torre_id BIGINT NOT NULL,
    nome_torre VARCHAR(100) NOT NULL,
    medidor_id BIGINT NOT NULL,
    consumo NUMERIC(12, 3) NOT NULL,
    percentual NUMERIC(8, 2) NOT NULL,
    valor_rateado NUMERIC(14, 2) NOT NULL,
    CONSTRAINT fk_itens_rateio_rateio
        FOREIGN KEY (rateio_id)
        REFERENCES rateios(id)
);
