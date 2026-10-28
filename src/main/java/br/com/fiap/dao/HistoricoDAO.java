package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Historico;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HistoricoDAO {

    private Connection minhaConexao;

    public HistoricoDAO() throws SQLException, ClassNotFoundException {
        minhaConexao = new ConexaoFactory().conexao();
    }

    // CREATE
    public String inserir(Historico historico, String email) throws SQLException {

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "INSERT INTO HISTORICO " +
                        "(CODIGO_VOUCHER, PONTOS, DATA_RESATE, EMAIL) " +
                        "VALUES (?, ?, ?, ?)"
        );

        stmt.setString(1, historico.getCodigo());
        stmt.setInt(2, historico.getPontos());
        stmt.setString(3, historico.getData());
        stmt.setString(4, email);

        stmt.executeUpdate();
        stmt.close();

        return "Historico cadastrado com sucesso!";
    }

    // READ
    public ArrayList<Historico> listar(String email) throws SQLException {

        ArrayList<Historico> historicos = new ArrayList<>();

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "SELECT CODIGO_VOUCHER, PONTOS, DATA_RESATE " +
                        "FROM HISTORICO WHERE EMAIL = ?"
        );

        stmt.setString(1, email);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {

            Historico historico = new Historico();

            historico.setCodigo(
                    rs.getString("CODIGO_VOUCHER"));

            historico.setPontos(
                    rs.getInt("PONTOS"));

            historico.setData(
                    rs.getString("DATA_RESATE"));

            historicos.add(historico);
        }

        rs.close();
        stmt.close();

        return historicos;
    }

    // UPDATE
    public String atualizar(Historico historico, String email,
                            String codigoAntigo) throws SQLException {

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "UPDATE HISTORICO SET " +
                        "CODIGO_VOUCHER = ?, " +
                        "PONTOS = ?, " +
                        "DATA_RESATE = ? " +
                        "WHERE CODIGO_VOUCHER = ? AND EMAIL = ?"
        );

        stmt.setString(1, historico.getCodigo());
        stmt.setInt(2, historico.getPontos());
        stmt.setString(3, historico.getData());
        stmt.setString(4, codigoAntigo);
        stmt.setString(5, email);

        stmt.executeUpdate();
        stmt.close();

        return "Historico atualizado com sucesso!";
    }

    // DELETE
    public String deletar(String codigoVoucher, String email)
            throws SQLException {

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "DELETE FROM HISTORICO " +
                        "WHERE CODIGO_VOUCHER = ? AND EMAIL = ?"
        );

        stmt.setString(1, codigoVoucher);
        stmt.setString(2, email);

        stmt.executeUpdate();
        stmt.close();

        return "Historico deletado com sucesso!";
    }
}