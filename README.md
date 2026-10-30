# SoulUp API — Entrega Final

API RESTful em **Java 21 + Spring Boot 3.4** para o projeto Eco Mobility SoulUp, com persistência em **Oracle** via JDBC.

Usuários ganham pontos ao concluir missões de mobilidade sustentável e trocam esses pontos por vouchers.

## Status da entrega

- **Build Maven:** concluído com sucesso (`mvn clean package`).
- **Deploy:** publicado com sucesso no Render.
- **Banco de dados:** conexão Oracle online.
- **Conclusão de missão:** transação testada com sucesso.
- **Resgate de voucher:** transação testada com sucesso.
- **Frontend React:** institucional, ainda sem integração com a API.

### API publicada

URL: https://soulup-api-78mu.onrender.com

Verificação da aplicação e do banco:

https://soulup-api-78mu.onrender.com/api/status/banco

Resultado verificado:

```json
{
  "banco": "online",
  "aplicacao": "SoulUp API"
}
```

## Arquitetura

```text
br.com.fiap
├── config      CorsFilter
├── conexao     ConexaoFactory
├── entities    Usuario, Missao, Voucher, Historico
├── dto         LoginRequest e respostas de conclusão/resgate
├── dao         UsuarioDAO, MissaoDAO, VoucherDAO,
│               HistoricoDAO, MissaoUsuarioDAO
├── bo          UsuarioBO, MissaoBO, VoucherBO, HistoricoBO
├── exception   Exceções de negócio
└── resource    Endpoints REST + GlobalExceptionHandler
```

Fluxo de uma requisição: **Resource → BO → DAO → ConexaoFactory → Oracle**.

- **Resource**: recebe a requisição e devolve a resposta. Não contém regras de negócio.
- **BO**: valida os dados e aplica as regras (e-mail único, missão diária uma vez por dia, saldo de pontos etc.).
- **DAO**: executa SQL com `PreparedStatement`; cada método abre e fecha a própria conexão com `try-with-resources`.
- **GlobalExceptionHandler**: converte exceções em status HTTP, no formato `{"erro": "mensagem"}`.

### Regras de negócio principais

- Usuário novo começa com 0 pontos; o e-mail é único e não pode ser alterado.
- Concluir missão e resgatar voucher são operações transacionais: registro, pontos e histórico são gravados juntos ou nada é gravado.
- Missão diária só pode ser concluída uma vez por dia por usuário (409 se repetir).
- O desconto de pontos é condicionado ao saldo no próprio `UPDATE`, evitando saldo negativo.
- A senha nunca é devolvida nas respostas.

## Requisitos

- JDK 21
- Maven 3.9+
- Acesso ao Oracle e às tabelas `USUARIO`, `MISSAO`, `VOUCHER`, `HISTORICO` e `MISSAO_USUARIO`

## Configuração (variáveis de ambiente)

| Variável | Obrigatória | Descrição |
|---|---|---|
| `SOULUP_DB_USER` | Sim | Usuário do Oracle |
| `SOULUP_DB_PASSWORD` | Sim | Senha do Oracle |
| `SOULUP_DB_URL` | Não | URL JDBC (padrão: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl`) |
| `PORT` | Não | Porta HTTP (padrão `8080`; o Render define automaticamente) |

Nenhuma credencial fica no código.

## Executar localmente

Linux/macOS:

```bash
export SOULUP_DB_USER=seu_usuario
export SOULUP_DB_PASSWORD=sua_senha
mvn spring-boot:run
```

Windows (PowerShell):

```powershell
$env:SOULUP_DB_USER="seu_usuario"
$env:SOULUP_DB_PASSWORD="sua_senha"
mvn spring-boot:run
```

Gerar o JAR:

```bash
mvn clean package
java -jar target/soulup-api-1.0.0.jar
```

Verificação rápida:

- `GET /api/status` → verifica se a aplicação está no ar.
- `GET /api/status/banco` → verifica a conexão com o banco (200 ou 503).

## Deploy (Render)

1. Criar um Web Service a partir do repositório, usando o ambiente **Docker**.
2. O `Dockerfile` fica na raiz do projeto.
3. Em **Environment**, configurar `SOULUP_DB_USER`, `SOULUP_DB_PASSWORD` e, se necessário, `SOULUP_DB_URL`.
4. Após o deploy, validar com `GET {url}/api/status/banco`.

**Deploy realizado com sucesso:** serviço publicado e com conexão Oracle confirmada.

> No plano gratuito do Render, a primeira requisição após um período sem uso pode levar cerca de 1 minuto.

## Endpoints

| Método | Caminho | Função | Sucesso |
|---|---|---|---|
| GET | `/api/status` | Estado da API | 200 |
| GET | `/api/status/banco` | Estado da API e do banco | 200 / 503 |
| GET | `/api/usuarios` | Listar usuários | 200 |
| GET | `/api/usuarios/{email}` | Consultar usuário | 200 |
| POST | `/api/usuarios` | Cadastrar usuário | 201 |
| POST | `/api/usuarios/login` | Validar login | 200 |
| PUT | `/api/usuarios/{email}` | Atualizar usuário | 200 |
| DELETE | `/api/usuarios/{email}` | Excluir usuário | 204 |
| GET | `/api/missoes` | Listar missões | 200 |
| GET | `/api/missoes/{nome}` | Consultar missão | 200 |
| POST | `/api/missoes` | Cadastrar missão | 201 |
| PUT | `/api/missoes/{nome}` | Atualizar missão | 200 |
| DELETE | `/api/missoes/{nome}` | Excluir missão | 204 |
| POST | `/api/missoes/{nome}/conclusoes?email=` | Concluir missão para um usuário | 201 |
| GET | `/api/vouchers` | Listar vouchers | 200 |
| GET | `/api/vouchers/{codigo}` | Consultar voucher | 200 |
| POST | `/api/vouchers` | Cadastrar voucher | 201 |
| PUT | `/api/vouchers/{codigo}` | Atualizar voucher | 200 |
| DELETE | `/api/vouchers/{codigo}` | Excluir voucher | 204 |
| POST | `/api/vouchers/{codigo}/resgates?email=` | Resgatar voucher e registrar histórico | 201 |
| GET | `/api/historicos?email=` | Consultar histórico do usuário | 200 |
| POST | `/api/historicos?email=` | Registrar histórico | 201 |
| PUT | `/api/historicos/{codigo}?email=` | Atualizar registro | 200 |
| DELETE | `/api/historicos/{codigo}?email=` | Excluir registro | 204 |

Exemplos de requisições estão no arquivo `testes-api.http`.

## Testes realizados

Foram realizados testes da API localmente, utilizando dados existentes no banco Oracle.

- Consulta de usuários, missões e vouchers.
- Conclusão de missão com retorno HTTP 201.
- Resgate de voucher com retorno HTTP 201.
- Verificação da conexão com o Oracle localmente e no Render.
- Build com Maven concluído com sucesso.

Os testes de conclusão de missão e resgate de voucher confirmaram o funcionamento das respectivas operações transacionais.

## Códigos HTTP

| Código | Quando é usado |
|---|---|
| `200 OK` | Consultas, atualizações e login |
| `201 Created` | Cadastros, conclusão de missão e resgate de voucher |
| `204 No Content` | Exclusões e respostas OPTIONS (CORS) |
| `400 Bad Request` | Dados inválidos ou parâmetros obrigatórios ausentes |
| `401 Unauthorized` | E-mail ou senha inválidos no login |
| `404 Not Found` | Usuário, missão, voucher ou histórico inexistente |
| `405 Method Not Allowed` | Verbo HTTP não suportado |
| `409 Conflict` | Cadastro duplicado, pontos insuficientes ou missão diária repetida |
| `500 Internal Server Error` | Falha inesperada |

## Tabelas e colunas usadas

| Tabela | Colunas |
|---|---|
| `USUARIO` | `EMAIL`, `NOME`, `SENHA`, `PONTOS` |
| `MISSAO` | `NOME`, `DESCRICAO`, `PONTOS`, `TRANSPORTE`, `CO2`, `CONCLUSAO`, `DIARIA`, `DATA_CONCLUSAO` |
| `VOUCHER` | `CODIGO`, `DESCRICAO`, `PONTOS`, `VALOR` |
| `HISTORICO` | `CODIGO_VOUCHER`, `PONTOS`, `DATA_RESATE`, `EMAIL` |
| `MISSAO_USUARIO` | `EMAIL`, `NOME_MISSAO`, `DATA_CONCLUSAO` |

## Observações

- O frontend React entregue é institucional e ainda não realiza chamadas a esta API.
- A integração entre React e API não faz parte das funcionalidades implementadas nesta entrega.
- As senhas são armazenadas conforme o modelo original do banco de dados (texto puro). Em um ambiente de produção real, recomenda-se armazenar senhas utilizando hash seguro, como BCrypt.
- As credenciais do banco são fornecidas por variáveis de ambiente e não devem ser publicadas no repositório.
