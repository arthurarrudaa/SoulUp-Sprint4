package br.com.fiap.resource;

import br.com.fiap.exception.ConflitoException;
import br.com.fiap.exception.CredenciaisInvalidasException;
import br.com.fiap.exception.RecursoNaoEncontradoException;
import br.com.fiap.exception.RegraNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Converte as exceções da aplicação em respostas HTTP padronizadas: {"erro": "mensagem"}.
 * As Resources e BOs só lançam exceções; o status HTTP é decidido aqui.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // Códigos de erro do Oracle
    private static final int ORA_REGISTRO_DUPLICADO = 1;
    private static final int ORA_CHAVE_PAI_INEXISTENTE = 2291;
    private static final int ORA_REGISTRO_FILHO_EXISTE = 2292;

    // ---------- Exceções de negócio ----------

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Map<String, String>> naoEncontrado(RecursoNaoEncontradoException e) {
        return resposta(HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Map<String, String>> conflito(ConflitoException e) {
        return resposta(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Map<String, String>> regraNegocio(RegraNegocioException e) {
        return resposta(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<Map<String, String>> naoAutorizado(CredenciaisInvalidasException e) {
        return resposta(HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    // ---------- Banco de dados ----------

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<Map<String, String>> erroBanco(SQLException e) {
        switch (e.getErrorCode()) {
            case ORA_REGISTRO_DUPLICADO:
                log.warn("Registro duplicado: {}", e.getMessage());
                return resposta(HttpStatus.CONFLICT, "Registro já cadastrado.");
            case ORA_CHAVE_PAI_INEXISTENTE:
                log.warn("Referência inexistente: {}", e.getMessage());
                return resposta(HttpStatus.BAD_REQUEST, "Registro relacionado não existe.");
            case ORA_REGISTRO_FILHO_EXISTE:
                log.warn("Registro com dependências: {}", e.getMessage());
                return resposta(HttpStatus.CONFLICT,
                        "O registro possui dependências e não pode ser excluído.");
            default:
                log.error("Erro ao acessar o banco de dados", e);
                return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados.");
        }
    }

    // ---------- Erros de requisição (Spring MVC) ----------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> jsonInvalido(HttpMessageNotReadableException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Corpo JSON inválido.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> parametroAusente(MissingServletRequestParameterException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Parâmetro obrigatório ausente: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> tipoInvalido(MethodArgumentTypeMismatchException e) {
        return resposta(HttpStatus.BAD_REQUEST, "Valor inválido para o parâmetro: " + e.getName());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, String>> metodoNaoPermitido(HttpRequestMethodNotSupportedException e) {
        return resposta(HttpStatus.METHOD_NOT_ALLOWED, "Método HTTP não permitido para esta rota.");
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> tipoNaoSuportado(HttpMediaTypeNotSupportedException e) {
        return resposta(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Envie o corpo da requisição como application/json.");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> rotaInexistente(NoResourceFoundException e) {
        return resposta(HttpStatus.NOT_FOUND, "Rota não encontrada.");
    }

    // ---------- Qualquer outro erro ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> erroGeral(Exception e) {
        log.error("Erro inesperado", e);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a solicitação.");
    }

    private ResponseEntity<Map<String, String>> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(Map.of("erro", mensagem));
    }
}
