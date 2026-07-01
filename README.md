![Java CI](https://github.com/armandor123/condominio-gas-rateio/actions/workflows/ci.yml/badge.svg)

# Sistema de Rateio de Gás Condominial

API REST desenvolvida em Java com Spring Boot para calcular o rateio de consumo de gás entre torres de um condomínio.

O projeto simula um cenário real de gestão condominial, onde existe um medidor principal de gás que registra o consumo total da concessionária e dois medidores secundários responsáveis por separar o consumo das torres Prime e Hype.

---

## Sobre o projeto

Em alguns condomínios, a conta de gás chega com base em um único medidor principal. Porém, internamente, existem medidores secundários instalados para identificar quanto cada torre consumiu.

Este sistema resolve esse problema calculando automaticamente quanto cada torre deve pagar de acordo com o consumo proporcional registrado nos medidores secundários.

### Cenário utilizado

O condomínio possui:

- 1 medidor principal da concessionária
- 1 medidor secundário para a Torre Prime
- 1 medidor secundário para a Torre Hype
- 1 conta mensal com o valor total a ser rateado

A API permite cadastrar torres, medidores, leituras mensais, contas mensais e calcular o rateio proporcional.

---

## Problema resolvido

A concessionária envia uma conta com base no consumo do medidor principal.

Exemplo:

```text
Valor total da conta: R$ 10.000,00
Consumo do medidor principal: 1200 m³

Consumo Torre Prime: 600 m³
Consumo Torre Hype: 400 m³
```

O sistema soma os consumos secundários:

```text
600 + 400 = 1000 m³
```

Calcula o percentual de cada torre:

```text
Prime = 600 / 1000 = 60%
Hype  = 400 / 1000 = 40%
```

E divide o valor da conta:

```text
Prime = R$ 10.000,00 * 60% = R$ 6.000,00
Hype  = R$ 10.000,00 * 40% = R$ 4.000,00
```

Também registra a diferença entre o medidor principal e os medidores secundários:

```text
1200 - 1000 = 200 m³
```

---

## Funcionalidades

- Cadastro de torres
- Cadastro de medidores
- Registro de leituras mensais
- Cadastro de contas mensais
- Cálculo automático de consumo
- Cálculo proporcional do rateio
- Consulta de rateios por mês
- Listagem de torres, medidores, leituras, contas e rateios
- Tratamento de erros de regra de negócio
- Documentação via Swagger
- Testes automatizados
- Relatório de cobertura com JaCoCo
- Execução com Docker
- Pipeline de CI com GitHub Actions

---

## Tecnologias utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- H2 Database
- Flyway
- Maven
- Docker
- Docker Compose
- JUnit 5
- Mockito
- MockMvc
- JaCoCo
- Swagger / OpenAPI
- GitHub Actions

---

## Arquitetura do projeto

O projeto foi organizado seguindo uma separação em camadas inspirada em Clean Architecture.

```text
src/main/java/br/com/armandorodrigues/gasrateio
├── application
│   ├── dto
│   └── usecase
├── domain
│   ├── exception
│   ├── model
│   ├── repository
│   └── service
├── infrastructure
│   ├── config
│   ├── persistence
│   │   ├── entity
│   │   ├── mapper
│   │   └── repository
│   └── report
└── interfaceadapter
    ├── controller
    └── exception
```

### Responsabilidades principais

| Camada | Responsabilidade |
|---|---|
| `domain` | Regras de negócio, entidades e serviços de domínio |
| `application` | Casos de uso e DTOs |
| `infrastructure` | Persistência, JPA, configurações e integrações |
| `interfaceadapter` | Controllers REST e tratamento de exceções |

---

## Principais regras de negócio

### Torre

- Nome da torre é obrigatório
- Nome deve ter pelo menos 2 caracteres
- Torres são criadas como ativas

### Medidor

- Código do medidor deve ser único
- Medidor principal não pode estar vinculado a uma torre
- Medidor secundário deve estar vinculado a uma torre
- Nome, código e tipo são obrigatórios

### Leitura

- Cada medidor só pode ter uma leitura por mês
- Leitura atual não pode ser menor que a leitura anterior
- Consumo é calculado automaticamente:

```text
consumo = leituraAtual - leituraAnterior
```

### Conta mensal

- Só pode existir uma conta mensal por mês de referência
- Valor total deve ser maior que zero
- Consumo informado não pode ser negativo
- Data de vencimento é obrigatória

### Rateio

- Só pode existir um rateio por mês
- Deve existir conta mensal cadastrada
- Deve existir leitura do medidor principal
- Deve existir leitura dos medidores secundários
- O valor é dividido proporcionalmente pelo consumo dos medidores secundários
- Diferenças de arredondamento são ajustadas no item de maior consumo

---

## Endpoints principais

### Torres

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/torres` | Cadastrar torre |
| `GET` | `/torres` | Listar torres |

### Medidores

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/medidores` | Cadastrar medidor |
| `GET` | `/medidores` | Listar medidores |

### Leituras

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/leituras` | Registrar leitura |
| `GET` | `/leituras` | Listar leituras |

### Contas mensais

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/contas-mensais` | Cadastrar conta mensal |
| `GET` | `/contas-mensais` | Listar contas mensais |

### Rateios

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/rateios/calcular` | Calcular rateio |
| `GET` | `/rateios` | Listar rateios |
| `GET` | `/rateios/{mesReferencia}` | Buscar rateio por mês |

---

## Como rodar o projeto localmente

### Pré-requisitos

- Java 21
- Maven Wrapper
- Docker
- Docker Compose
- Git

---

## Rodar com H2 Database

O projeto possui configuração com banco em memória H2 para execução rápida em ambiente local.

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

Console H2:

```text
http://localhost:8080/h2-console
```

Configuração do H2:

```text
JDBC URL: jdbc:h2:mem:gasrateiodb
User: sa
Password: vazio
```

---

## Rodar com PostgreSQL local via Docker

Suba apenas o banco PostgreSQL:

```bash
cd infra
docker compose up -d postgres
```

Volte para a raiz do projeto:

```bash
cd ..
```

Rode a aplicação com o profile `postgres`:

```bash
./mvnw -Dspring-boot.run.profiles=postgres spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:8080
```

---

## Rodar API + PostgreSQL com Docker

O projeto também pode ser executado completamente com Docker, subindo a API Spring Boot e o PostgreSQL juntos.

Acesse a pasta `infra`:

```bash
cd infra
```

Execute:

```bash
docker compose up --build
```

Esse comando irá:

- Construir a imagem da API
- Subir o PostgreSQL
- Aguardar o banco ficar saudável
- Subir a API na porta `8080`

Acesse:

```text
http://localhost:8080
```

Swagger:

```text
http://localhost:8080/swagger-ui.html
```

Para parar os containers:

```bash
docker compose down
```

Para parar e apagar os dados do banco:

```bash
docker compose down -v
```

Use `-v` apenas quando quiser apagar o volume do PostgreSQL.

---

## Swagger

A documentação interativa da API está disponível em:

```text
http://localhost:8080/swagger-ui.html
```

Documentação OpenAPI em JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Exemplos de uso da API

Abaixo está um fluxo completo de uso da API.

> Observação: caso os dados demonstrativos estejam ativados com `app.seed.demo-enabled=true`, algumas requisições podem retornar erro de duplicidade porque Prime, Hype, medidores e rateios de exemplo já podem existir.

---

### 1. Cadastrar Torre Prime

```bash
curl -X POST http://localhost:8080/torres \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Prime"
  }'
```

---

### 2. Cadastrar Torre Hype

```bash
curl -X POST http://localhost:8080/torres \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Hype"
  }'
```

---

### 3. Listar torres

```bash
curl http://localhost:8080/torres
```

Resposta esperada:

```json
[
  {
    "id": 1,
    "nome": "Hype",
    "ativa": true
  },
  {
    "id": 2,
    "nome": "Prime",
    "ativa": true
  }
]
```

> Os IDs podem variar conforme a ordem de cadastro e os dados já existentes no banco.

---

### 4. Cadastrar medidor principal

```bash
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Principal",
    "codigo": "GAS-PRINCIPAL-001",
    "tipo": "PRINCIPAL",
    "torreId": null
  }'
```

---

### 5. Cadastrar medidor da Torre Prime

Use o `torreId` retornado no cadastro/listagem de torres.

```bash
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Torre Prime",
    "codigo": "GAS-PRIME-001",
    "tipo": "SECUNDARIO",
    "torreId": 2
  }'
```

---

### 6. Cadastrar medidor da Torre Hype

Use o `torreId` retornado no cadastro/listagem de torres.

```bash
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Torre Hype",
    "codigo": "GAS-HYPE-001",
    "tipo": "SECUNDARIO",
    "torreId": 1
  }'
```

---

### 7. Listar medidores

```bash
curl http://localhost:8080/medidores
```

---

### 8. Registrar leitura do medidor principal

Use o `medidorId` retornado na listagem de medidores.

```bash
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 1,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 10000.00,
    "leituraAtual": 11200.00
  }'
```

Consumo calculado:

```text
11200 - 10000 = 1200
```

---

### 9. Registrar leitura da Torre Prime

```bash
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 2,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 5000.00,
    "leituraAtual": 5600.00
  }'
```

Consumo calculado:

```text
5600 - 5000 = 600
```

---

### 10. Registrar leitura da Torre Hype

```bash
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 3,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 4000.00,
    "leituraAtual": 4400.00
  }'
```

Consumo calculado:

```text
4400 - 4000 = 400
```

---

### 11. Cadastrar conta mensal

```bash
curl -X POST http://localhost:8080/contas-mensais \
  -H "Content-Type: application/json" \
  -d '{
    "mesReferencia": "2026-06",
    "valorTotal": 10000.00,
    "consumoInformado": 1200.00,
    "dataVencimento": "2026-07-10",
    "numeroFatura": "FAT-2026-06",
    "observacoes": "Conta referente ao consumo de junho."
  }'
```

---

### 12. Calcular rateio

```bash
curl -X POST http://localhost:8080/rateios/calcular \
  -H "Content-Type: application/json" \
  -d '{
    "mesReferencia": "2026-06"
  }'
```

Resultado esperado:

```json
{
  "mesReferencia": "2026-06",
  "valorTotalConta": 10000.00,
  "consumoTotalSecundario": 1000.00,
  "consumoMedidorPrincipal": 1200.00,
  "diferencaConsumo": 200.00,
  "itens": [
    {
      "nomeTorre": "Prime",
      "consumo": 600.00,
      "percentual": 60.00,
      "valorRateado": 6000.00
    },
    {
      "nomeTorre": "Hype",
      "consumo": 400.00,
      "percentual": 40.00,
      "valorRateado": 4000.00
    }
  ]
}
```

---

### 13. Buscar rateio por mês

```bash
curl http://localhost:8080/rateios/2026-06
```

---

### 14. Listar todos os rateios

```bash
curl http://localhost:8080/rateios
```

---

## Testes automatizados

O projeto possui testes automatizados cobrindo regras de negócio, casos de uso e controllers da API.

Foram implementados testes para:

### Entidades de domínio

- Torre
- Medidor
- Leitura
- ContaMensal

### Serviços de domínio

- CalculadoraRateio

### Casos de uso

- Cadastro de torres
- Cadastro de medidores
- Registro de leituras
- Registro de contas mensais
- Cálculo de rateio
- Busca de rateio por mês
- Listagem de torres
- Listagem de medidores
- Listagem de leituras
- Listagem de contas mensais
- Listagem de rateios

### Controllers

- TorreController
- MedidorController
- LeituraController
- ContaMensalController
- RateioController

### Tratamento de erros

- GlobalExceptionHandler

Para rodar os testes:

```bash
./mvnw test
```

Para rodar o build completo:

```bash
./mvnw clean install
```

---

## Cobertura de testes

O projeto utiliza JaCoCo para gerar relatório de cobertura de testes.

Para gerar o relatório:

```bash
./mvnw clean test
```

Após a execução, o relatório HTML será gerado em:

```text
target/site/jacoco/index.html
```

Para abrir no Linux:

```bash
xdg-open target/site/jacoco/index.html
```

A pasta `target/` não é versionada no Git, pois contém arquivos gerados automaticamente pelo Maven.

---

## Integração contínua

O projeto possui pipeline de CI com GitHub Actions.

A cada `push` ou `pull request` para as branches `main` ou `master`, o GitHub executa automaticamente:

```bash
./mvnw clean test
```

Isso garante que os testes sejam executados automaticamente antes de novas alterações serem integradas.

---

## Banco de dados

O projeto utiliza Flyway para versionamento do banco de dados.

As migrations ficam em:

```text
src/main/resources/db/migration
```

Exemplo:

```text
V1__create_initial_schema.sql
V2__add_ativa_to_torres.sql
```

---

## Dados demonstrativos

O projeto possui inicialização de dados demonstrativos controlada pela propriedade:

```properties
app.seed.demo-enabled=true
```

Quando ativada, a aplicação cria dados de exemplo para facilitar testes manuais, como:

- Torre Prime
- Torre Hype
- Medidor principal
- Medidor Torre Prime
- Medidor Torre Hype
- Leituras
- Contas mensais
- Rateios demonstrativos

Para desativar os dados demonstrativos:

```properties
app.seed.demo-enabled=false
```

---

## Estrutura Docker

O projeto possui:

```text
Dockerfile
infra/docker-compose.yml
src/main/resources/application-docker.properties
.dockerignore
```

O `Dockerfile` cria a imagem da API Spring Boot.

O `docker-compose.yml` sobe:

- PostgreSQL
- API Spring Boot

A API usa o profile:

```text
docker
```

---

## Aprendizados demonstrados neste projeto

Este projeto demonstra conhecimentos em:

- Desenvolvimento de API REST com Spring Boot
- Modelagem de domínio
- Separação de responsabilidades em camadas
- Regras de negócio com Java
- Persistência com Spring Data JPA
- Versionamento de banco com Flyway
- Testes unitários
- Testes de controllers com MockMvc
- Tratamento global de exceções
- Dockerização de aplicação Java
- Docker Compose com múltiplos serviços
- Integração contínua com GitHub Actions
- Documentação de API com Swagger
- Boas práticas para projeto de portfólio

---

## Próximas melhorias

Possíveis evoluções futuras:

- Criar autenticação com Spring Security
- Criar perfis de usuário
- Criar tela frontend para operação da API
- Gerar relatório PDF do rateio mensal
- Exportar rateio para Excel
- Criar histórico de alterações
- Criar endpoint para dashboard
- Adicionar paginação e filtros
- Publicar a aplicação em ambiente cloud
- Criar documentação com imagens do Swagger

---

## Autor

Desenvolvido por Armando Rodrigues.

Projeto criado como parte do portfólio de estudos em Java, Spring Boot, APIs REST, Docker, testes automatizados e boas práticas de desenvolvimento backend.

