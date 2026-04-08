<h1 align="center" style="font-weight: bold;">Products-Java-Spring 💻</h1>

<p align="center">
 <a href="#tech">Tecnologias</a> • 
 <a href="#env">Variáveis de Ambiente</a> • 
 <a href="#docker">Docker</a> •
 <a href="#started">Getting Started</a> • 
 <a href="#routes">API Endpoints</a>
</p>

<p align="center">
    <b>CRUD de uma API REST de estoque de produtos.</b>
</p>

<h2 id="tech">💻 Tecnologias</h2>

Lista de todas as tecnologias usadas
- Java
- Spring JPA
- Spring Validation
- Lombok
- MapStruct
- PostgreSQL
- Docker
- Docker Compose

<h2 id="docker">🐳 Docker</h2>

A aplicação pode ser executada utilizando **Docker** e **Docker Compose**, permitindo subir toda a infraestrutura necessária (API + Banco de Dados) de forma simples e padronizada.

### Build da imagem da aplicação

```bash
docker build -t products-java-spring .
```

<h2 id="env">⚙️ Variáveis de Ambiente</h2>
<h3>Configuração de Variáveis de Ambiente</h3>

A aplicação utiliza **variáveis de ambiente** para configurar a conexão com o banco de dados.

Antes de iniciar o projeto, configure as seguintes variáveis:

| Variável | Descrição | Exemplo |
|--------|--------|--------|
| DB_URL | URL do banco de dados | jdbc:postgresql://localhost:5432/productsdb |
| DB | Nome do banco | productsdb |
| DB_USERNAME | Usuário do banco | postgres |
| DB_PASSWORD | Senha do banco | postgres |

### Exemplo de arquivo `.env`

Você pode criar um arquivo `.env` na raiz do projeto com as seguintes variáveis:

```env
DB_URL=jdbc:postgresql://localhost:5432/productsdb
DB=productsdb
DB_USERNAME=postgres
DB_PASSWORD=postgres
```

<h2 id="started">🚀 Getting started</h2>

<h3>Pré-requisitos</h3>

Lista dos pré-requisitos para rodar o projeto:

- [Java](https://www.java.com/pt-BR/download)
- [Git](https://git-scm.com/downloads)
- [Docker](https://docs.docker.com/desktop/setup/install/windows-install/)

<h3>Clonagem</h3>

Como clonar o projeto:

```bash
git clone https://github.com/KahoanDev/Products-Java-Spring.git
```

<h3>Iniciando</h3>

#### Ambiente Local

1. Build the project:
   ```bash
   mvn clean package
   ```
2. Run the application:
   ```bash
   mvn spring-boot:run
   ```

#### Ambiente com Docker

1. Run the application with Docker Compose:
   ```bash
   docker-compose up --build
   ```
O Docker Compose irá subir dois containers:

- **PostgreSQL** → banco de dados
- **API Spring Boot** → aplicação Java

A API ficará disponível em:
```bash
http://localhost:8081
```

<h2 id="routes">📍 API Endpoints</h2>

​
| route               | description                                          
|----------------------|-----------------------------------------------------
| <kbd>GET /produto/{id}</kbd>     | Pesquisa por Id [response details](#get-auth-detail)
| <kbd>GET /produto</kbd>     | Pesquisa todos [response details](#get2-auth-detail)
| <kbd>POST /produto</kbd>     | Salvar [request details](#post-auth-detail)
| <kbd>PUT /produto/{id}</kbd>     | Atualizar [request details](#put-auth-detail)
| <kbd>DELETE /produto/{id}</kbd>     | Deletar [details](#delete-auth-detail)

<h3 id="get-auth-detail">GET /produto/{id}</h3>

**RESPONSE**
```json
{
  "id": 1,
  "descricao": "Veja Limpa Tudo",
  "tipo": "LIMPEZA",
  "quantidade": 58
}
```

<h3 id="get2-auth-detail">GET /produto</h3>

**RESPONSE**
```json
[
  {
        "id": 1,
        "descricao": "Veja Limpa Tudo",
        "tipo": "LIMPEZA",
        "quantidade": 58
    },
    {
        "id": 2,
        "descricao": "Neve Papel Higienico",
        "tipo": "HIGIENE",
        "quantidade": 12
    }
]
```

<h3 id="post-auth-detail">POST /produto</h3>

**REQUEST**
```json
{
    "descricao": "Veja Limpa Tudo",
    "tipo": "LIMPEZA",
    "quantidade": 58
}
```

<h3 id="put-auth-detail">PUT /produto/{id}</h3>

**REQUEST**
```json
{
    "descricao": "Veja Limpa MAX",
    "tipo": "LIMPEZA",
    "quantidade": 120
}
```

<h3 id="delete-auth-detail">DELETE /produto/{id}</h3>
