package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Missao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MissaoDAO {
    Connection minhaConexao;

    public MissaoDAO() throws SQLException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Missao missao) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "INSERT INTO MISSAO VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
        );

        stmt.setString(1, missao.getNome());
        stmt.setString(2, missao.getDescricao());
        stmt.setInt(3, missao.getPontos());
        stmt.setString(4, missao.getTransporte());
        stmt.setDouble(5, missao.getCo2());
        stmt.setInt(6, missao.getConclusoes());
        stmt.setInt(7, missao.isDiaria() ? 1 : 0);
        stmt.setString(8, missao.getDataConclusao());

        stmt.execute();
        stmt.close();

        return "Missão cadastrada com sucesso!";
    }

    public ArrayList<Missao> listar() throws SQLException {
        ArrayList<Missao> missoes = new ArrayList<>();

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "SELECT * FROM MISSAO"
        );

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Missao missao = new Missao();

            missao.setNome(rs.getString("NOME"));
            missao.setDescricao(rs.getString("DESCRICAO"));
            missao.setPontos(rs.getInt("PONTOS"));
            missao.setTransporte(rs.getString("TRANSPORTE"));
            missao.setCo2(rs.getDouble("CO2"));
            missao.setConclusoes(rs.getInt("CONCLUSAO"));
            missao.setDiaria(rs.getInt("DIARIA") == 1);
            missao.setDataConclusao(rs.getString("DATA_CONCLUSAO"));

            missoes.add(missao);
        }

        rs.close();
        stmt.close();

        return missoes;
    }

    public String atualizar(Missao missao) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "UPDATE MISSAO SET DESCRICAO = ?, PONTOS = ?, TRANSPORTE = ?, CO2 = ?, CONCLUSAO = ?, DIARIA = ?, DATA_CONCLUSAO = ? WHERE NOME = ?"
        );

        stmt.setString(1, missao.getDescricao());
        stmt.setInt(2, missao.getPontos());
        stmt.setString(3, missao.getTransporte());
        stmt.setDouble(4, missao.getCo2());
        stmt.setInt(5, missao.getConclusoes());
        stmt.setInt(6, missao.isDiaria() ? 1 : 0);
        stmt.setString(7, missao.getDataConclusao());
        stmt.setString(8, missao.getNome());

        stmt.executeUpdate();
        stmt.close();

        return "Missão atualizada com sucesso!";
    }

    public String deletar(String nome) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "DELETE FROM MISSAO WHERE NOME = ?"
        );

        stmt.setString(1, nome);

        stmt.executeUpdate();
        stmt.close();

        return "Missão deletada com sucesso!";
    }
}
