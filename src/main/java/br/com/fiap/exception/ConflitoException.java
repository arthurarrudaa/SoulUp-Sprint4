package br.com.fiap.exception;

/** Lançada quando a operação conflita com o estado atual, como cadastro duplicado (HTTP 409). */
public class ConflitoException extends RuntimeException {

    public ConflitoException(String mensagem) {
        super(mensagem);
    }
}
