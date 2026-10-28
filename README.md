# SoulUp API — Sprint 4

API REST em Java 21 e Spring Boot, com persistência Oracle e camadas de entidades, DAO, BO e Resource.

## Requisitos
- JDK 21
- Maven 3.9+
- Acesso ao Oracle FIAP e tabelas do projeto

## Configuração do banco
As credenciais podem ser definidas por variáveis de ambiente `SOULUP_DB_URL`, `SOULUP_DB_USER` e `SOULUP_DB_PASSWORD`. Na ausência delas, a aplicação usa a configuração Oracle já utilizada pelo projeto original.

## Executar

Na pasta que contém o arquivo `pom.xml`, execute a aplicação com Maven:

```bash
mvn spring-boot:run
```

Para gerar o arquivo JAR:

```bash
mvn clean package
```

Depois de gerar o JAR, execute-o com:

```bash
java -jar target/soulup-api-1.0.0.jar
```

A aplicação estará disponível em:

```text
http://localhost:8080
```

Para verificar se está funcionando, acesse:

```text
http://localhost:8080/api/status
```

## Endpoints
| Método | Caminho | Função |
|---|---|---|
| GET | `/api/status` | Estado da API |
| GET | `/api/usuarios` | Listar usuários |
| GET | `/api/usuarios/{email}` | Consultar usuário |
| POST | `/api/usuarios` | Cadastrar usuário |
| POST | `/api/usuarios/login` | Validar login |
| PUT | `/api/usuarios/{email}` | Atualizar usuário |
| DELETE | `/api/usuarios/{email}` | Excluir usuário |
| GET | `/api/missoes` | Listar missões |
| GET | `/api/missoes/{nome}` | Consultar missão |
| POST | `/api/missoes` | Cadastrar missão |
| PUT | `/api/missoes/{nome}` | Atualizar missão |
| DELETE | `/api/missoes/{nome}` | Excluir missão |
| POST | `/api/missoes/{nome}/conclusoes?email=...` | Concluir missão para usuário |
| GET | `/api/vouchers` | Listar vouchers |
| POST | `/api/vouchers/{codigo}/resgates?email=...` | Resgatar voucher e registrar histórico |
| GET | `/api/vouchers/{codigo}` | Consultar voucher |
| POST | `/api/vouchers` | Cadastrar voucher |
| PUT | `/api/vouchers/{codigo}` | Atualizar voucher |
| DELETE | `/api/vouchers/{codigo}` | Excluir voucher |
| GET | `/api/historicos?email=...` | Consultar histórico de usuário |
| POST | `/api/historicos?email=...` | Registrar histórico |
| PUT | `/api/historicos/{codigo}?email=...` | Atualizar registro |
| DELETE | `/api/historicos/{codigo}?email=...` | Excluir registro |

## Códigos HTTP

A API utiliza os seguintes códigos de status HTTP:

| Código | Significado | Quando é utilizado |
|---|---|---|
| `200 OK` | Requisição concluída | Consultas, atualizações e operações concluídas com sucesso |
| `201 Created` | Recurso criado | Cadastro de usuários, missões ou vouchers |
| `204 No Content` | Sucesso sem conteúdo | Exclusão de recursos |
| `400 Bad Request` | Requisição inválida | Dados ausentes ou que não atendem às validações |
| `401 Unauthorized` | Não autorizado | E-mail ou senha inválidos no login |
| `404 Not Found` | Recurso não encontrado | Usuário, missão, voucher ou registro inexistente |
| `409 Conflict` | Conflito | Cadastro duplicado ou conclusão repetida de missão diária |
| `500 Internal Server Error` | Erro interno | Falha inesperada no processamento da requisição |

Os códigos efetivamente retornados podem variar conforme o endpoint e a situação encontrada.

## Observações
O site React entregue é institucional e ainda não faz chamadas a esta API. A integração das páginas institucionais com os recursos de usuário/missões/vouchers não está implementada no front-end. O endpoint de conclusão de missão depende da tabela `MISSAO_USUARIO` com as colunas `EMAIL`, `NOME_MISSAO` e `DATA_CONCLUSAO`. O histórico depende da coluna `EMAIL` na tabela `HISTORICO`, além das colunas usadas pelo DAO original.
