# Sistema Bancario Java Completo

Sistema bancario desktop desenvolvido em Java com interface grafica Swing e persistencia de dados em PostgreSQL.

## Descricao

Aplicacao desktop que gerencia contas bancarias (corrente e poupanca), clientes, usuarios e operacoes financeiras (deposito, saque, transferencia, rendimento). Toda a interacao ocorre por meio de telas Swing, sem uso de terminal ou console. Os dados sao persistidos em banco PostgreSQL com acesso via JDBC na camada DAO.

## Tecnologias Utilizadas

- Java 21
- Maven 3.x
- PostgreSQL 14+
- JDBC (driver postgresql 42.7.3)
- Swing (nativo Java)
- IDE: Visual Studio Code

## Como Compilar e Executar

### Pre-requisitos

- Java 21 instalado
- Maven instalado
- PostgreSQL instalado e em execucao
- Banco de dados `sistema_banco` criado:

```sql
CREATE DATABASE sistema_banco;
```

### Configuracao do banco

1. Copie o arquivo de exemplo de configuracao:

```
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

2. Edite `src/main/resources/db.properties` com suas credenciais:

```properties
db.url=jdbc:postgresql://localhost:5432/sistema_banco
db.usuario=postgres
db.senha=sua_senha_aqui
```

> **Atencao:** O arquivo `db.properties` esta no `.gitignore` e nunca deve ser commitado.

### Compilar

```bash
mvn clean package
```

### Executar

```bash
java -jar target/sistema-banco.jar
```

Ou diretamente pelo Maven:

```bash
mvn exec:java -Dexec.mainClass="banco.app.SistemaBanco"
```

### Executar no VSCode

Abra o projeto no VSCode com a extensao "Extension Pack for Java" instalada e execute a classe `banco.app.SistemaBanco`.

## Login Inicial

| Campo | Valor |
|-------|-------|
| Login | `victorhugo` |
| Senha | `20252021847` |
| Perfil | ADMIN |

> Este usuario e criado automaticamente na primeira execucao e nao pode ser excluido pelo sistema.

## Estrutura de Pacotes

```
src/main/java/banco/
├── model/          → Entidades do sistema
│   ├── Usuario.java
│   ├── Cliente.java
│   ├── ContaBancaria.java   (abstrata, implementa Operavel)
│   ├── ContaCorrente.java
│   └── ContaPoupanca.java
├── interfaces/     → Contratos do sistema
│   └── Operavel.java
├── service/        → Regras de negocio
│   ├── BancoService.java
│   └── UsuarioService.java
├── dao/            → Acesso ao banco de dados (JDBC)
│   ├── ConexaoDB.java         (Singleton)
│   ├── InicializadorBanco.java
│   ├── UsuarioDAO.java
│   ├── ClienteDAO.java
│   ├── ContaCorrenteDAO.java
│   ├── ContaPoupancaDAO.java
│   └── TransacaoDAO.java
├── ui/             → Telas Swing
│   ├── Cores.java
│   ├── ComponenteFactory.java
│   ├── TelaLogin.java
│   ├── TelaMenuPrincipal.java
│   ├── TelaCadastroUsuario.java
│   ├── TelaGerenciarUsuarios.java
│   ├── TelaCadastroCliente.java
│   ├── TelaCadastroContaCorrente.java
│   ├── TelaCadastroContaPoupanca.java
│   ├── TelaOperacoes.java
│   ├── TelaExtrato.java
│   └── TelaRelatorio.java
└── app/            → Ponto de entrada
    └── SistemaBanco.java
```

## Hierarquia de Classes

```
java.lang.Object
└── ContaBancaria (abstrata, implements Operavel)
    ├── ContaCorrente
    └── ContaPoupanca

Operavel (interface)
    └── depositar(double)
    └── sacar(double) : boolean
    └── exibirSaldo(JLabel)
```

## Responsabilidade das Classes

| Classe | Responsabilidade |
|--------|-----------------|
| `SistemaBanco` | Ponto de entrada; inicializa banco e abre TelaLogin |
| `InicializadorBanco` | Cria tabelas e insere usuario administrador inicial |
| `ConexaoDB` | Singleton que gerencia a conexao JDBC |
| `UsuarioDAO` | CRUD de usuarios no banco |
| `ClienteDAO` | CRUD de clientes no banco |
| `ContaCorrenteDAO` | CRUD de contas correntes |
| `ContaPoupancaDAO` | CRUD de contas poupanca |
| `TransacaoDAO` | Registra e consulta historico de transacoes |
| `UsuarioService` | Autenticacao, hash SHA-256, regras de negocio de usuarios |
| `BancoService` | Operacoes bancarias, transferencias, relatorios |
| `ContaBancaria` | Modelo abstrato de conta com saldo e historico |
| `ContaCorrente` | Conta com cheque especial |
| `ContaPoupanca` | Conta com taxa de rendimento mensal |

## Aluno

**Nome:** Victor Hugo Rodrigues Silverio
**Turma:** 2 Modulo - POO
**Instituicao:** CEFET-MG - Unidade Campo Belo
