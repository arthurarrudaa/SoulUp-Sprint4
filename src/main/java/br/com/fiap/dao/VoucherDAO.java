package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Voucher;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class VoucherDAO {

    private static final String COLUNAS = "CODIGO, DESCRICAO, PONTOS, VALOR";

    private final ConexaoFactory conexaoFactory;

    public VoucherDAO(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // CREATE
    public void inserir(Voucher voucher) throws SQLException {
        String sql = "INSERT INTO VOUCHER (" + COLUNAS + ") VALUES (?, ?, ?, ?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, voucher.getCodigo());
            stmt.setString(2, voucher.getDescricao());
            stmt.setInt(3, voucher.getPontos());
            stmt.setDouble(4, voucher.getValor());
            stmt.executeUpdate();
        }
    }

    // READ
    public List<Voucher> listar() throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM VOUCHER ORDER BY PONTOS";
        List<Voucher> vouchers = new ArrayList<>();

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                vouchers.add(mapear(rs));
            }
        }
        return vouchers;
    }

    public Optional<Voucher> buscarPorCodigo(String codigo) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM VOUCHER WHERE LOWER(CODIGO) = LOWER(?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // UPDATE
    public boolean atualizar(Voucher voucher) throws SQLException {
        String sql = "UPDATE VOUCHER SET DESCRICAO = ?, PONTOS = ?, VALOR = ? WHERE CODIGO = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, voucher.getDescricao());
            stmt.setInt(2, voucher.getPontos());
            stmt.setDouble(3, voucher.getValor());
            stmt.setString(4, voucher.getCodigo());
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(String codigo) throws SQLException {
        String sql = "DELETE FROM VOUCHER WHERE CODIGO = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            return stmt.executeUpdate() > 0;
        }
    }

    private Voucher mapear(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setCodigo(rs.getString("CODIGO"));
        voucher.setDescricao(rs.getString("DESCRICAO"));
        voucher.setPontos(rs.getInt("PONTOS"));
        voucher.setValor(rs.getDouble("VALOR"));
        return voucher;
    }
}
