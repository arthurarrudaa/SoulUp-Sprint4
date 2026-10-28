package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UsuarioDAO {
    Connection minhaConexao;

    public UsuarioDAO() throws SQLException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Usuario usuario) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "INSERT INTO USUARIO VALUES (?, ?, ?, ?)"
        );

        stmt.setString(1, usuario.getEmail());
        stmt.setString(2, usuario.getNome());
        stmt.setString(3, usuario.getSenha());
        stmt.setInt(4, usuario.getPontos());

        stmt.execute();
        stmt.close();

        return "Usuário cadastrado com sucesso!";
    }

    public ArrayList<Usuario> listar() throws SQLException {
        ArrayList<Usuario> usuarios = new ArrayList<>();

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "SELECT * FROM USUARIO"
        );

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Usuario usuario = new Usuario();

            usuario.setEmail(rs.getString("EMAIL"));
            usuario.setNome(rs.getString("NOME"));
            usuario.setSenha(rs.getString("SENHA"));
            usuario.setPontos(rs.getInt("PONTOS"));

            usuarios.add(usuario);
        }

        rs.close();
        stmt.close();

        return usuarios;
    }

    public String atualizar(Usuario usuario) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "UPDATE USUARIO SET NOME = ?, SENHA = ?, PONTOS = ? WHERE EMAIL = ?"
        );

        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getSenha());
        stmt.setInt(3, usuario.getPontos());
        stmt.setString(4, usuario.getEmail());

        stmt.executeUpdate();
        stmt.close();

        return "Usuário atualizado com sucesso!";
    }

    public String deletar(String email) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "DELETE FROM USUARIO WHERE EMAIL = ?"
        );

        stmt.setString(1, email);

        stmt.executeUpdate();
        stmt.close();

        return "Usuário deletado com sucesso!";
    }


    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT EMAIL, NOME, SENHA, PONTOS FROM USUARIO WHERE LOWER(EMAIL) = LOWER(?)";

        try (PreparedStatement stmt = minhaConexao.prepareStatement(sql)) {
            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setEmail(rs.getString("EMAIL"));
                    usuario.setNome(rs.getString("NOME"));
                    usuario.setSenha(rs.getString("SENHA"));
                    usuario.setPontos(rs.getInt("PONTOS"));
                    return usuario;
                }
            }
        }

        return null;
    }
}
