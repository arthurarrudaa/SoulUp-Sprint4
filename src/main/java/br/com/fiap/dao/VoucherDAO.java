package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Voucher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VoucherDAO {
    Connection minhaConexao;

    public VoucherDAO() throws SQLException {
        this.minhaConexao = new ConexaoFactory().conexao();
    }

    public String inserir(Voucher voucher) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "INSERT INTO VOUCHER VALUES (?, ?, ?, ?)"
        );

        stmt.setString(1, voucher.getCodigo());
        stmt.setString(2, voucher.getDescricao());
        stmt.setInt(3, voucher.getPontos());
        stmt.setDouble(4, voucher.getValor());

        stmt.execute();
        stmt.close();

        return "Voucher cadastrado com sucesso!";
    }

    public ArrayList<Voucher> listar() throws SQLException {
        ArrayList<Voucher> vouchers = new ArrayList<>();

        PreparedStatement stmt = minhaConexao.prepareStatement(
                "SELECT * FROM VOUCHER"
        );

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Voucher voucher = new Voucher();

            voucher.setCodigo(rs.getString("CODIGO"));
            voucher.setDescricao(rs.getString("DESCRICAO"));
            voucher.setPontos(rs.getInt("PONTOS"));
            voucher.setValor(rs.getDouble("VALOR"));

            vouchers.add(voucher);
        }

        rs.close();
        stmt.close();

        return vouchers;
    }

    public String atualizar(Voucher voucher) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "UPDATE VOUCHER SET DESCRICAO = ?, PONTOS = ?, VALOR = ? WHERE CODIGO = ?"
        );

        stmt.setString(1, voucher.getDescricao());
        stmt.setInt(2, voucher.getPontos());
        stmt.setDouble(3, voucher.getValor());
        stmt.setString(4, voucher.getCodigo());

        stmt.executeUpdate();
        stmt.close();

        return "Voucher atualizado com sucesso!";
    }

    public String deletar(String codigo) throws SQLException {
        PreparedStatement stmt = minhaConexao.prepareStatement(
                "DELETE FROM VOUCHER WHERE CODIGO = ?"
        );

        stmt.setString(1, codigo);

        stmt.executeUpdate();
        stmt.close();

        return "Voucher deletado com sucesso!";
    }
}
