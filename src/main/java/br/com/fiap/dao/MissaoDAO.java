package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Missao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class MissaoDAO {

    private static final String COLUNAS =
            "NOME, DESCRICAO, PONTOS, TRANSPORTE, CO2, CONCLUSAO, DIARIA, DATA_CONCLUSAO";

    private final ConexaoFactory conexaoFactory;

    public MissaoDAO(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // CREATE
    public void inserir(Missao missao) throws SQLException {
        String sql = "INSERT INTO MISSAO (" + COLUNAS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, missao.getNome());
            stmt.setString(2, missao.getDescricao());
            stmt.setInt(3, missao.getPontos());
            stmt.setString(4, missao.getTransporte());
            stmt.setDouble(5, missao.getCo2());
            stmt.setInt(6, missao.getConclusoes());
            stmt.setInt(7, missao.isDiaria() ? 1 : 0);
            stmt.setString(8, missao.getDataConclusao());
            stmt.executeUpdate();
        }
    }

    // READ
    public List<Missao> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM MISSAO ORDER BY NOME";
        List<Missao> missoes = new ArrayList<>();

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                missoes.add(mapear(rs));
            }
        }
        return missoes;
    }

    public Optional<Missao> buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM MISSAO WHERE LOWER(NOME) = LOWER(?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nome);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // UPDATE
    public boolean atualizar(Missao missao) throws SQLException {
        String sql = "UPDATE MISSAO SET DESCRICAO = ?, PONTOS = ?, TRANSPORTE = ?, CO2 = ?, "
                + "CONCLUSAO = ?, DIARIA = ?, DATA_CONCLUSAO = ? WHERE NOME = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, missao.getDescricao());
            stmt.setInt(2, missao.getPontos());
            stmt.setString(3, missao.getTransporte());
            stmt.setDouble(4, missao.getCo2());
            stmt.setInt(5, missao.getConclusoes());
            stmt.setInt(6, missao.isDiaria() ? 1 : 0);
            stmt.setString(7, missao.getDataConclusao());
            stmt.setString(8, missao.getNome());
            return stmt.executeUpdate() > 0;
        }
    }

    /** Soma 1 ao contador de conclusões dentro de uma transação aberta pela BO. */
    public void incrementarConclusoes(Connection con, String nome) throws SQLException {
        String sql = "UPDATE MISSAO SET CONCLUSAO = CONCLUSAO + 1 WHERE NOME = ?";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, nome);
            stmt.executeUpdate();
        }
    }

    // DELETE
    public boolean deletar(String nome) throws SQLException {
        String sql = "DELETE FROM MISSAO WHERE NOME = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, nome);
            return stmt.executeUpdate() > 0;
        }
    }

    private Missao mapear(ResultSet rs) throws SQLException {
        Missao missao = new Missao();
        missao.setNome(rs.getString("NOME"));
        missao.setDescricao(rs.getString("DESCRICAO"));
        missao.setPontos(rs.getInt("PONTOS"));
        missao.setTransporte(rs.getString("TRANSPORTE"));
        missao.setCo2(rs.getDouble("CO2"));
        missao.setConclusoes(rs.getInt("CONCLUSAO"));
        missao.setDiaria(rs.getInt("DIARIA") == 1);
        missao.setDataConclusao(rs.getString("DATA_CONCLUSAO"));
        return missao;
    }
}
