package br.com.fiap.exception;

/** Lançada quando e-mail ou senha do login estão incorretos (HTTP 401). */
public class CredenciaisInvalidasException extends RuntimeException {

    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }
}
