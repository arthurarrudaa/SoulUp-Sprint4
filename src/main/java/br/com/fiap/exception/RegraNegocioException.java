package br.com.fiap.exception;

/** Lançada quando os dados ou uma regra de negócio são violados (HTTP 400). */
public class RegraNegocioException extends RuntimeException {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
