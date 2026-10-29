package br.com.fiap.conexao;

import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Connection Factory: único ponto de criação de conexões com o Oracle.
 * Quem recebe a conexão é responsável por fechá-la (try-with-resources).
 *
 * Variáveis de ambiente:
 *  - SOULUP_DB_URL      (opcional, padrão: Oracle da FIAP)
 *  - SOULUP_DB_USER     (obrigatória)
 *  - SOULUP_DB_PASSWORD (obrigatória)
 */
@Component
public class ConexaoFactory {

    private static final String URL_PADRAO = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
    private static final int TIMEOUT_LOGIN_SEGUNDOS = 15;

    public Connection conexao() throws SQLException {
        String url = System.getenv("SOULUP_DB_URL");
        String usuario = System.getenv("SOULUP_DB_USER");
        String senha = System.getenv("SOULUP_DB_PASSWORD");

        if (vazio(url)) {
            url = URL_PADRAO;
        }

        if (vazio(usuario) || vazio(senha)) {
            throw new SQLException("Configure as variáveis de ambiente SOULUP_DB_USER e SOULUP_DB_PASSWORD.");
        }

        DriverManager.setLoginTimeout(TIMEOUT_LOGIN_SEGUNDOS);
        return DriverManager.getConnection(url, usuario, senha);
    }

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }
}
