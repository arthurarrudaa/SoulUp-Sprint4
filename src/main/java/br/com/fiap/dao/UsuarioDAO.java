package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDAO {

    private static final String COLUNAS = "EMAIL, NOME, SENHA, PONTOS";

    private final ConexaoFactory conexaoFactory;

    public UsuarioDAO(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // CREATE
    public void inserir(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO USUARIO (" + COLUNAS + ") VALUES (?, ?, ?, ?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, usuario.getNome());
            stmt.setString(3, usuario.getSenha());
            stmt.setInt(4, usuario.getPontos());
            stmt.executeUpdate();
        }
    }

    // READ
    public List<Usuario> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM USUARIO ORDER BY NOME";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(mapear(rs));
            }
        }
        return usuarios;
    }

    public Optional<Usuario> buscarPorEmail(String email) throws SQLException {
        try (Connection con = conexaoFactory.conexao()) {
            return buscarPorEmail(con, email);
        }
    }

    /** Versão para uso dentro de uma transação aberta pela BO. */
    public Optional<Usuario> buscarPorEmail(Connection con, String email) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM USUARIO WHERE LOWER(EMAIL) = LOWER(?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // UPDATE
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE USUARIO SET NOME = ?, SENHA = ?, PONTOS = ? WHERE EMAIL = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getSenha());
            stmt.setInt(3, usuario.getPontos());
            stmt.setString(4, usuario.getEmail());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Soma pontos de forma atômica (sem ler e regravar o saldo). */
    public boolean adicionarPontos(Connection con, String email, int pontos) throws SQLException {
        String sql = "UPDATE USUARIO SET PONTOS = PONTOS + ? WHERE EMAIL = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pontos);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Subtrai pontos de forma atômica; devolve false se o saldo for insuficiente. */
    public boolean removerPontos(Connection con, String email, int pontos) throws SQLException {
        String sql = "UPDATE USUARIO SET PONTOS = PONTOS - ? WHERE EMAIL = ? AND PONTOS >= ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, pontos);
            stmt.setString(2, email);
            stmt.setInt(3, pontos);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(String email) throws SQLException {
        String sql = "DELETE FROM USUARIO WHERE EMAIL = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            return stmt.executeUpdate() > 0;
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setEmail(rs.getString("EMAIL"));
        usuario.setNome(rs.getString("NOME"));
        usuario.setSenha(rs.getString("SENHA"));
        usuario.setPontos(rs.getInt("PONTOS"));
        return usuario;
    }
}
