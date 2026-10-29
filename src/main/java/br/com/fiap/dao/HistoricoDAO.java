package br.com.fiap.dao;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.entities.Historico;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class HistoricoDAO {

    /** Nome da coluna de data na tabela HISTORICO (mantido como no banco original). */
    private static final String COLUNA_DATA = "DATA_RESATE";

    private final ConexaoFactory conexaoFactory;

    public HistoricoDAO(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    // CREATE
    public void inserir(Historico historico, String email) throws SQLException {
        try (Connection con = conexaoFactory.conexao()) {
            inserir(con, historico, email);
        }
    }

    /** Versão para uso dentro de uma transação aberta pela BO. */
    public void inserir(Connection con, Historico historico, String email) throws SQLException {
        String sql = "INSERT INTO HISTORICO (CODIGO_VOUCHER, PONTOS, " + COLUNA_DATA + ", EMAIL) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, historico.getCodigo());
            stmt.setInt(2, historico.getPontos());
            stmt.setString(3, historico.getData());
            stmt.setString(4, email);
            stmt.executeUpdate();
        }
    }

    // READ
    public List<Historico> listar(String email) throws SQLException {
        String sql = "SELECT CODIGO_VOUCHER, PONTOS, " + COLUNA_DATA + " FROM HISTORICO WHERE EMAIL = ?";
        List<Historico> historicos = new ArrayList<>();

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    historicos.add(mapear(rs));
                }
            }
        }
        return historicos;
    }

    public Optional<Historico> buscar(String email, String codigo) throws SQLException {
        String sql = "SELECT CODIGO_VOUCHER, PONTOS, " + COLUNA_DATA + " FROM HISTORICO "
                + "WHERE EMAIL = ? AND LOWER(CODIGO_VOUCHER) = LOWER(?)";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, codigo);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    // UPDATE
    public boolean atualizar(Historico historico, String email, String codigoAntigo) throws SQLException {
        String sql = "UPDATE HISTORICO SET CODIGO_VOUCHER = ?, PONTOS = ?, " + COLUNA_DATA + " = ? "
                + "WHERE CODIGO_VOUCHER = ? AND EMAIL = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, historico.getCodigo());
            stmt.setInt(2, historico.getPontos());
            stmt.setString(3, historico.getData());
            stmt.setString(4, codigoAntigo);
            stmt.setString(5, email);
            return stmt.executeUpdate() > 0;
        }
    }

    // DELETE
    public boolean deletar(String codigo, String email) throws SQLException {
        String sql = "DELETE FROM HISTORICO WHERE CODIGO_VOUCHER = ? AND EMAIL = ?";

        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        }
    }

    private Historico mapear(ResultSet rs) throws SQLException {
        Historico historico = new Historico();
        historico.setCodigo(rs.getString("CODIGO_VOUCHER"));
        historico.setPontos(rs.getInt("PONTOS"));
        historico.setData(rs.getString(COLUNA_DATA));
        return historico;
    }
}
