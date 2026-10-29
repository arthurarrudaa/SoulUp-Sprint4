# SoulUp API — Entrega Final

API RESTful em **Java 21 + Spring Boot 3.4** para o projeto Eco Mobility SoulUp, com persistência em **Oracle** via JDBC.
Usuários ganham pontos ao concluir missões de mobilidade sustentável e trocam esses pontos por vouchers.

## Arquitetura

```
br.com.fiap
├── config      CorsFilter                      (liberação de CORS para o front-end)
├── conexao     ConexaoFactory                  (Connection Factory do Oracle)
├── entities    Usuario, Missao, Voucher, Historico   (camada modelo)
├── dto         LoginRequest e respostas de conclusão/resgate
├── dao         UsuarioDAO, MissaoDAO, VoucherDAO, HistoricoDAO, MissaoUsuarioDAO
├── bo          UsuarioBO, MissaoBO, VoucherBO, HistoricoBO   (validações e regras de negócio)
├── exception   exceções de negócio (404, 409, 400, 401)
└── resource    endpoints REST + GlobalExceptionHandler
```

Fluxo de uma requisição: **Resource → BO → DAO → ConexaoFactory → Oracle**.

- **Resource**: só recebe a requisição e devolve a resposta. Não tem regra de negócio.
- **BO**: valida os dados e aplica as regras (e-mail único, missão diária uma vez por dia, saldo de pontos, etc.).
- **DAO**: SQL com `PreparedStatement`; cada método abre e fecha a própria conexão (`try-with-resources`).
- **GlobalExceptionHandler**: único lugar que converte exceções em status HTTP, sempre no formato `{"erro": "mensagem"}`.

### Regras de negócio principais
- Usuário novo sempre começa com 0 pontos; o e-mail é único e não pode ser alterado.
- Concluir missão e resgatar voucher rodam em **transação**: registro, pontos e histórico são gravados juntos ou nada é gravado.
- Missão diária só pode ser concluída uma vez por dia por usuário (409 se repetir).
- O desconto de pontos é condicionado ao saldo no próprio `UPDATE`, então o saldo nunca fica negativo.
- A senha nunca é devolvida nas respostas.

## Requisitos
- JDK 21
- Maven 3.9+
- Acesso ao Oracle e às tabelas `USUARIO`, `MISSAO`, `VOUCHER`, `HISTORICO` e `MISSAO_USUARIO`

## Configuração (variáveis de ambiente)

| Variável | Obrigatória | Descrição |
|---|---|---|
| `SOULUP_DB_USER` | sim | Usuário do Oracle |
| `SOULUP_DB_PASSWORD` | sim | Senha do Oracle |
| `SOULUP_DB_URL` | não | URL JDBC (padrão: `jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl`) |
| `PORT` | não | Porta HTTP (padrão `8080`; o Render define automaticamente) |

Nenhuma credencial fica no código.

## Executar localmente

```bash
# Linux/macOS
export SOULUP_DB_USER=seu_usuario
export SOULUP_DB_PASSWORD=sua_senha
mvn spring-boot:run
```

```powershell
# Windows (PowerShell)
$env:SOULUP_DB_USER="seu_usuario"
$env:SOULUP_DB_PASSWORD="sua_senha"
mvn spring-boot:run
```

Gerar o JAR: `mvn clean package` e depois `java -jar target/soulup-api-1.0.0.jar`.

Verificação rápida:
- `GET /api/status` → a aplicação está no ar.
- `GET /api/status/banco` → a aplicação também conseguiu consultar o banco (200 ou 503).

## Deploy (Render)
1. Criar um *Web Service* a partir do repositório (ambiente **Docker**; o `Dockerfile` já está na raiz).
2. Em *Environment*, definir `SOULUP_DB_USER`, `SOULUP_DB_PASSWORD` e, se necessário, `SOULUP_DB_URL`.
3. Após o deploy, validar com `GET {url}/api/status/banco`.

> No plano gratuito do Render a primeira requisição após um período sem uso pode levar cerca de 1 minuto.

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
| POST | `/api/vouchers` | Cadastrar voucher (código gerado se omitido) | 201 |
| PUT | `/api/vouchers/{codigo}` | Atualizar voucher | 200 |
| DELETE | `/api/vouchers/{codigo}` | Excluir voucher | 204 |
| POST | `/api/vouchers/{codigo}/resgates?email=` | Resgatar voucher e registrar histórico | 201 |
| GET | `/api/historicos?email=` | Consultar histórico do usuário | 200 |
| POST | `/api/historicos?email=` | Registrar histórico | 201 |
| PUT | `/api/historicos/{codigo}?email=` | Atualizar registro | 200 |
| DELETE | `/api/historicos/{codigo}?email=` | Excluir registro | 204 |

Exemplos prontos para executar estão em `testes-api.http`.

## Códigos HTTP

| Código | Quando é usado |
|---|---|
| `200 OK` | Consultas, atualizações e login |
| `201 Created` | Cadastros, conclusão de missão e resgate de voucher |
| `204 No Content` | Exclusões e respostas `OPTIONS` (CORS) |
| `400 Bad Request` | Dados inválidos, JSON malformado, parâmetro obrigatório ausente |
| `401 Unauthorized` | E-mail ou senha inválidos no login |
| `404 Not Found` | Usuário, missão, voucher ou histórico inexistente |
| `405 Method Not Allowed` | Verbo HTTP não suportado na rota |
| `409 Conflict` | Cadastro duplicado, pontos insuficientes, missão diária repetida, registro com dependências |
| `500 Internal Server Error` | Falha inesperada (detalhes ficam no log, nunca na resposta) |

## Tabelas e colunas usadas

| Tabela | Colunas |
|---|---|
| `USUARIO` | `EMAIL`, `NOME`, `SENHA`, `PONTOS` |
| `MISSAO` | `NOME`, `DESCRICAO`, `PONTOS`, `TRANSPORTE`, `CO2`, `CONCLUSAO`, `DIARIA`, `DATA_CONCLUSAO` |
| `VOUCHER` | `CODIGO`, `DESCRICAO`, `PONTOS`, `VALOR` |
| `HISTORICO` | `CODIGO_VOUCHER`, `PONTOS`, `DATA_RESATE`, `EMAIL` |
| `MISSAO_USUARIO` | `EMAIL`, `NOME_MISSAO`, `DATA_CONCLUSAO` |

## Observações
- O site React entregue é institucional e ainda não faz chamadas a esta API.
- As senhas são armazenadas como no banco original (texto puro). Em produção real, deveriam usar hash (BCrypt).
