package br.com.fiap.bo;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.dao.HistoricoDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.VoucherDAO;
import br.com.fiap.dto.ResgateVoucherResponse;
import br.com.fiap.entities.Historico;
import br.com.fiap.entities.Usuario;
import br.com.fiap.entities.Voucher;
import br.com.fiap.exception.ConflitoException;
import br.com.fiap.exception.RecursoNaoEncontradoException;
import br.com.fiap.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
public class VoucherBO {

    private static final int TENTATIVAS_CODIGO = 20;

    private final Random random = new Random();

    private final VoucherDAO voucherDAO;
    private final HistoricoDAO historicoDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioBO usuarioBO;
    private final ConexaoFactory conexaoFactory;

    public VoucherBO(VoucherDAO voucherDAO,
                     HistoricoDAO historicoDAO,
                     UsuarioDAO usuarioDAO,
                     UsuarioBO usuarioBO,
                     ConexaoFactory conexaoFactory) {
        this.voucherDAO = voucherDAO;
        this.historicoDAO = historicoDAO;
        this.usuarioDAO = usuarioDAO;
        this.usuarioBO = usuarioBO;
        this.conexaoFactory = conexaoFactory;
    }

    public List<Voucher> listar() throws SQLException {
        return voucherDAO.listar();
    }

    public Voucher buscar(String codigo) throws SQLException {
        if (vazio(codigo)) {
            throw new RegraNegocioException("O código do voucher é obrigatório.");
        }
        return voucherDAO.buscarPorCodigo(codigo.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Voucher não encontrado."));
    }

    public Voucher cadastrar(Voucher voucher) throws SQLException {
        validar(voucher);

        if (vazio(voucher.getCodigo())) {
            voucher.setCodigo(gerarCodigoUnico());
        } else {
            voucher.setCodigo(voucher.getCodigo().trim());
            if (voucherDAO.buscarPorCodigo(voucher.getCodigo()).isPresent()) {
                throw new ConflitoException("Voucher já cadastrado.");
            }
        }

        voucherDAO.inserir(voucher);
        return voucher;
    }

    public Voucher atualizar(String codigo, Voucher dados) throws SQLException {
        Voucher existente = buscar(codigo);

        validar(dados);
        dados.setCodigo(existente.getCodigo());

        voucherDAO.atualizar(dados);
        return dados;
    }

    public void excluir(String codigo) throws SQLException {
        Voucher existente = buscar(codigo);
        voucherDAO.deletar(existente.getCodigo());
    }

    /**
     * Resgata o voucher: desconta os pontos e grava o histórico na mesma transação.
     * O desconto é condicionado ao saldo no próprio UPDATE, então dois resgates
     * simultâneos não deixam o saldo negativo.
     */
    public ResgateVoucherResponse resgatar(String codigo, String email) throws SQLException {
        Voucher voucher = buscar(codigo);
        Usuario usuario = usuarioBO.buscar(email);
        int pontosRestantes = 0;

        try (Connection con = conexaoFactory.conexao()) {
            con.setAutoCommit(false);

            try {
                if (!usuarioDAO.removerPontos(con, usuario.getEmail(), voucher.getPontos())) {
                    throw new ConflitoException("Pontos insuficientes para resgatar este voucher.");
                }

                Historico historico = new Historico(
                        voucher.getCodigo(), voucher.getPontos(), LocalDate.now().toString());
                historicoDAO.inserir(con, historico, usuario.getEmail());

                pontosRestantes = usuarioDAO.buscarPorEmail(con, usuario.getEmail())
                        .map(Usuario::getPontos)
                        .orElse(0);

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }

        return new ResgateVoucherResponse("Voucher resgatado com sucesso.", voucher, pontosRestantes);
    }

    private String gerarCodigoUnico() throws SQLException {
        for (int i = 0; i < TENTATIVAS_CODIGO; i++) {
            String codigo = "SOULUP" + (1000 + random.nextInt(9000));
            if (voucherDAO.buscarPorCodigo(codigo).isEmpty()) {
                return codigo;
            }
        }
        throw new ConflitoException("Não foi possível gerar um código único. Tente novamente.");
    }

    private void validar(Voucher voucher) {
        if (voucher == null || vazio(voucher.getDescricao())) {
            throw new RegraNegocioException("Descrição do voucher é obrigatória.");
        }
        if (voucher.getPontos() <= 0 || voucher.getValor() < 0) {
            throw new RegraNegocioException("Pontos devem ser positivos e valor não pode ser negativo.");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
