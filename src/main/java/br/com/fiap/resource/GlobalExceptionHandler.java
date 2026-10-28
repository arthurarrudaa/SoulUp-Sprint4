
package br.com.fiap.resource;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(HttpMessageNotReadableException.class)
 public ResponseEntity<?> jsonInvalido(HttpMessageNotReadableException e) {
  return ResponseEntity.badRequest()
          .body(Map.of("erro", "Corpo JSON inválido."));
 }

 @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
 public ResponseEntity<?> metodoNaoPermitido(
         HttpRequestMethodNotSupportedException e) {
  return ResponseEntity.status(405)
          .body(Map.of("erro",
                  "Método HTTP não permitido para esta rota."));
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<?> erroGeral(Exception e) {
  e.printStackTrace();

  return ResponseEntity.status(500)
          .body(Map.of("erro",
                  "Erro interno ao processar a solicitação."));
 }
}
