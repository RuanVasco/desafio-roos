# User Data Processing API

---

## Pré-requisitos

### 1. Docker

- Docker Engine: Testado com a versão 28.2.2.
- Docker Compose: Testado com a versão 2.37.1.

### 2. Local

- JDK: Testado com 25.0.2.
- Maven: Testado com a versão 3.9.12 (via wrapper ./api/mvnw).

---

## Passo a passo para instalação

### 1. Instalação
- ```git clone https://github.com/RuanVasco/desafio-roos.git```.
- ```cd desafio-roos```.

### 2. Execução via Docker
- Na raiz do projeto: ```docker compose up --build```.

### 3. Execução Local (necessário o JDK 25)
- ```cd api```.
- ```./mvnw spring-boot:run```.

---

## Testar os endpoints
Existem dois endpoints:
- POST /users/import
- GET /users/{id}

### 1. Swagger
- Acesse ```http://localhost:8080/swagger-ui/index.html```.
- Será possível fazer as requisições via interface web.

### 2. CURL
- POST
```
  curl -X POST http://localhost:8080/users/import \
  -F "url=https://raw.githubusercontent.com/Sementes-Roos/user-data-processing-api/refs/heads/main/mock-data.json"
  ```
- GET
```
  curl -X GET http://localhost:8080/users/5df38f6e695566a48211da8f
```

---

## Notas sobre o código

- A lógica de processamento do JSON está no método "processUsersFromUrl" no UserService.
- Os dois endpoints estão em UserController.
- Abstração via JPA/Hibernate.
- Utilizando SQLite.
