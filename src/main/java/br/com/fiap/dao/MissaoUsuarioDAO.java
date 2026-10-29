package br.com.fiap.dao;

import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Acesso à tabela MISSAO_USUARIO (missões concluídas por cada usuário).
 * Os métodos recebem a conexão da BO para participar da mesma transação.
 */
@Repository
public class MissaoUsuarioDAO {

    public boolean existeConclusao(Connection con, String email, String nomeMissao, String data)
            throws SQLException {

        String sql = "SELECT COUNT(*) FROM MISSAO_USUARIO "
                + "WHERE EMAIL = ? AND NOME_MISSAO = ? AND DATA_CONCLUSAO = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, nomeMissao);
            stmt.setString(3, data);

            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public void inserir(Connection con, String email, String nomeMissao, String data)
            throws SQLException {

        String sql = "INSERT INTO MISSAO_USUARIO (EMAIL, NOME_MISSAO, DATA_CONCLUSAO) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, nomeMissao);
            stmt.setString(3, data);
            stmt.executeUpdate();
        }
    }
}
