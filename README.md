# Sistema de Rateio de Gás Condominial

API desenvolvida em Java com Spring Boot para calcular o rateio mensal da conta de gás entre torres de um condomínio.

## Sobre o projeto

Este projeto nasceu a partir de uma necessidade real de gestão condominial.

O condomínio possui um medidor principal de gás, responsável por registrar o consumo total de duas torres. Após esse medidor principal, existem dois medidores secundários, um para cada torre:

- Torre Prime
- Torre Hype

A conta da concessionária é gerada com base no medidor principal, porém a divisão interna precisa ser feita proporcionalmente ao consumo registrado nos medidores secundários.

O sistema automatiza esse processo, reduzindo cálculos manuais e aumentando a rastreabilidade do rateio mensal.

---

## Problema resolvido

# Sistema de Rateio de Gás Condominial

API desenvolvida em Java com Spring Boot para calcular o rateio mensal da conta de gás entre torres de um condomínio.

## Sobre o projeto

Este projeto nasceu a partir de uma necessidade real de gestão condominial.

O condomínio possui um medidor principal de gás, responsável por registrar o consumo total de duas torres. Após esse medidor principal, existem dois medidores secundários, um para cada torre:

- Torre Prime
- Torre Hype

A conta da concessionária é gerada com base no medidor principal, porém a divisão interna precisa ser feita proporcionalmente ao consumo registrado nos medidores secundários.

O sistema automatiza esse processo, reduzindo cálculos manuais e aumentando a rastreabilidade do rateio mensal.

---

## Problema resolvido

Antes do sistema, o cálculo precisava ser feito manualmente:

1. Verificar a conta mensal da concessionária.
2. Consultar o consumo do medidor principal.
3. Consultar o consumo dos medidores das torres.
4. Calcular a participação percentual de cada torre.
5. Aplicar esse percentual sobre o valor total da conta.
6. Conferir diferenças entre o consumo principal e a soma dos secundários.

Com a API, esse processo passa a ser automatizado.

---

## Exemplo de cálculo

Conta mensal de gás:

```text
Valor total da conta: R$ 10.000,00
Consumo do medidor principal: 1200 m³
Torre Prime: 600 m³
Torre Hype: 400 m³
Total secundário: 1000 m³

Cálculo proporcional
Prime = 600 / 1000 = 60%
Hype = 400 / 1000 = 40%

Resultado do rateio
Prime = R$ 6.000,00
Hype = R$ 4.000,00

O sistema também registra a diferença entre o consumo do medidor principal e a soma dos medidores secundários: 1200 - 1000 = 200 m³

Essa diferença pode indicar perda técnica, divergência de leitura, vazamento, erro de medição ou defasagem entre datas de leitura.


Funcionalidades
Cadastro de torres
Cadastro de medidores
Classificação de medidor principal e secundário
Registro de leituras mensais
Cálculo automático do consumo mensal
Cadastro da conta mensal da concessionária
Cálculo proporcional do rateio entre torres
Consulta de rateios calculados
Consulta de rateio por mês
Persistência em PostgreSQL via Docker
Documentação da API com Swagger/OpenAPI

Tecnologias utilizadas
Java 21
Spring Boot
Spring Web
Spring Data JPA
Bean Validation
PostgreSQL
H2 Database
Docker
Docker Compose
Maven
Swagger/OpenAPI
Git e GitHub

Arquitetura
O projeto foi organizado seguindo uma estrutura inspirada em Clean Architecture simplificada.
gas-rateio-api
└── src
    └── main
        └── java
            └── br.com.armandorodrigues.gasrateio
                ├── domain
                ├── application
                ├── infrastructure
                └── interfaceadapter


Camadas
Domain

Contém as regras de negócio principais do sistema.

Exemplos:

Torre
Medidor
Leitura
ContaMensal
Rateio
ItemRateio
CalculadoraRateio
Application

Contém os casos de uso da aplicação.

Exemplos:

CadastrarTorreUseCase
RegistrarLeituraUseCase
RegistrarContaMensalUseCase
CalcularRateioUseCase
BuscarRateioPorMesUseCase
Infrastructure

Contém detalhes técnicos de persistência, banco de dados, configurações e integrações.

Exemplos:

Entidades JPA
Repositórios Spring Data
Mappers
Configuração OpenAPI
Interface Adapter

Contém os controllers REST e tratamento de exceções.

Exemplos:

TorreController
MedidorController
LeituraController
ContaMensalController
RateioController
GlobalExceptionHandler
Como rodar o projeto
Pré-requisitos

Antes de começar, é necessário ter instalado:

Java 21
Maven
Docker
Docker Compose
Git
Subir o PostgreSQL com Docker

Na raiz do projeto, acesse a pasta infra:

cd infra

Suba o banco de dados:

docker compose up -d

Verifique se o container está rodando:

docker ps

O container esperado é:

gas-rateio-postgres

Neste projeto, o PostgreSQL está exposto na porta local 5434.

Rodar a API com PostgreSQL

Acesse a pasta da API:

cd ../gas-rateio-api

Execute a aplicação usando o profile postgres:

./mvnw spring-boot:run -Dspring-boot.run.profiles=postgres

A API ficará disponível em:

http://localhost:8080
Documentação Swagger

Com a aplicação rodando, acesse:

http://localhost:8080/swagger-ui.html

A especificação OpenAPI também pode ser acessada em:

http://localhost:8080/v3/api-docs
Endpoints principais
Torres
POST /torres
GET /torres
Medidores
POST /medidores
GET /medidores
Leituras
POST /leituras
GET /leituras
Contas mensais
POST /contas-mensais
GET /contas-mensais
Rateios
POST /rateios/calcular
GET /rateios
GET /rateios/{mesReferencia}
Exemplo de fluxo de uso
1. Cadastrar torres
curl -X POST http://localhost:8080/torres \
  -H "Content-Type: application/json" \
  -d '{"nome": "Prime"}'
curl -X POST http://localhost:8080/torres \
  -H "Content-Type: application/json" \
  -d '{"nome": "Hype"}'
2. Cadastrar medidores
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Principal",
    "codigo": "GAS-PRINCIPAL-001",
    "tipo": "PRINCIPAL"
  }'
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Torre Prime",
    "codigo": "GAS-PRIME-001",
    "tipo": "SECUNDARIO",
    "torreId": 1
  }'
curl -X POST http://localhost:8080/medidores \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Medidor Torre Hype",
    "codigo": "GAS-HYPE-001",
    "tipo": "SECUNDARIO",
    "torreId": 2
  }'
3. Registrar leituras
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 1,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 10000.00,
    "leituraAtual": 11200.00
  }'
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 2,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 5000.00,
    "leituraAtual": 5600.00
  }'
curl -X POST http://localhost:8080/leituras \
  -H "Content-Type: application/json" \
  -d '{
    "medidorId": 3,
    "mesReferencia": "2026-06",
    "dataLeitura": "2026-06-30",
    "leituraAnterior": 4000.00,
    "leituraAtual": 4400.00
  }'
4. Cadastrar conta mensal
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
5. Calcular rateio
curl -X POST http://localhost:8080/rateios/calcular \
  -H "Content-Type: application/json" \
  -d '{
    "mesReferencia": "2026-06"
  }'
6. Consultar rateio por mês
curl http://localhost:8080/rateios/2026-06
Regras de negócio implementadas
Não permitir cadastro de torre duplicada.
Não permitir cadastro de medidor com código duplicado.
Medidor principal não deve estar vinculado a uma torre.
Medidor secundário deve estar vinculado a uma torre.
Leitura atual não pode ser menor que leitura anterior.
Não permitir leitura duplicada para o mesmo medidor e mês.
Não permitir conta mensal duplicada para o mesmo mês.
Não permitir rateio duplicado para o mesmo mês.
O consumo total dos medidores secundários deve ser maior que zero.
O valor total rateado deve fechar com o valor total da conta.
Diferenças de arredondamento são ajustadas no item de maior consumo.
Próximas melhorias
Criar dados iniciais automáticos para demonstração.
Adicionar Flyway para versionamento do banco de dados.
Criar testes automatizados com JUnit e Mockito.
Criar geração de relatório em PDF.
Criar exportação em Excel.
Criar front-end em React com TypeScript.
Adicionar autenticação e controle de usuários.
Criar dashboard com histórico mensal de consumo e valores.

## Testes automatizados

O projeto possui testes automatizados cobrindo regras de negócio, casos de uso e controllers da API.

Foram implementados testes para:

- Entidades de domínio:
  - Torre
  - Medidor
  - Leitura
  - ContaMensal

- Serviços de domínio:
  - CalculadoraRateio

- Casos de uso:
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

- Controllers:
  - TorreController
  - MedidorController
  - LeituraController
  - ContaMensalController
  - RateioController

- Tratamento de erros:
  - GlobalExceptionHandler

Para rodar os testes:

```bash
./mvnw test

Autor

Desenvolvido por Armando Rodrigues.

Projeto criado como parte de portfólio de desenvolvimento backend, utilizando uma necessidade real de gestão condominial como base para a solução.
