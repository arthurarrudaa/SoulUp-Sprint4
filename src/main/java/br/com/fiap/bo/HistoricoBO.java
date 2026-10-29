package br.com.fiap.bo;

import br.com.fiap.dao.HistoricoDAO;
import br.com.fiap.entities.Historico;
import br.com.fiap.entities.Usuario;
import br.com.fiap.exception.RecursoNaoEncontradoException;
import br.com.fiap.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class HistoricoBO {

    private final HistoricoDAO historicoDAO;
    private final UsuarioBO usuarioBO;
    private final VoucherBO voucherBO;

    public HistoricoBO(HistoricoDAO historicoDAO, UsuarioBO usuarioBO, VoucherBO voucherBO) {
        this.historicoDAO = historicoDAO;
        this.usuarioBO = usuarioBO;
        this.voucherBO = voucherBO;
    }

    public List<Historico> listar(String email) throws SQLException {
        Usuario usuario = usuarioBO.buscar(email);
        return historicoDAO.listar(usuario.getEmail());
    }

    public Historico registrar(String email, Historico historico) throws SQLException {
        Usuario usuario = usuarioBO.buscar(email);

        if (historico == null || vazio(historico.getCodigo())) {
            throw new RegraNegocioException("O código do voucher é obrigatório.");
        }
        validarPontos(historico);

        // Garante que o voucher existe e usa o código exatamente como está no banco.
        historico.setCodigo(voucherBO.buscar(historico.getCodigo()).getCodigo());

        if (vazio(historico.getData())) {
            historico.setData(LocalDate.now().toString());
        }

        historicoDAO.inserir(historico, usuario.getEmail());
        return historico;
    }

    public Historico atualizar(String codigo, String email, Historico dados) throws SQLException {
        Usuario usuario = usuarioBO.buscar(email);
        Historico existente = buscarDoUsuario(usuario.getEmail(), codigo);

        if (dados == null) {
            throw new RegraNegocioException("Os dados do histórico são obrigatórios.");
        }
        validarPontos(dados);

        if (vazio(dados.getCodigo())) {
            dados.setCodigo(existente.getCodigo());
        } else {
            dados.setCodigo(voucherBO.buscar(dados.getCodigo()).getCodigo());
        }
        if (vazio(dados.getData())) {
            dados.setData(existente.getData());
        }

        historicoDAO.atualizar(dados, usuario.getEmail(), existente.getCodigo());
        return dados;
    }

    public void excluir(String codigo, String email) throws SQLException {
        Usuario usuario = usuarioBO.buscar(email);
        Historico existente = buscarDoUsuario(usuario.getEmail(), codigo);
        historicoDAO.deletar(existente.getCodigo(), usuario.getEmail());
    }

    private Historico buscarDoUsuario(String email, String codigo) throws SQLException {
        if (vazio(codigo)) {
            throw new RegraNegocioException("O código do voucher é obrigatório.");
        }
        return historicoDAO.buscar(email, codigo.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de histórico não encontrado."));
    }

    private void validarPontos(Historico historico) {
        if (historico.getPontos() < 0) {
            throw new RegraNegocioException("Os pontos não podem ser negativos.");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
