package br.com.fiap.bo;

import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.MissaoUsuarioDAO;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dto.ConclusaoMissaoResponse;
import br.com.fiap.entities.Missao;
import br.com.fiap.entities.Usuario;
import br.com.fiap.exception.ConflitoException;
import br.com.fiap.exception.RecursoNaoEncontradoException;
import br.com.fiap.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Service
public class MissaoBO {

    private final MissaoDAO missaoDAO;
    private final MissaoUsuarioDAO missaoUsuarioDAO;
    private final UsuarioDAO usuarioDAO;
    private final UsuarioBO usuarioBO;
    private final ConexaoFactory conexaoFactory;

    public MissaoBO(MissaoDAO missaoDAO,
                    MissaoUsuarioDAO missaoUsuarioDAO,
                    UsuarioDAO usuarioDAO,
                    UsuarioBO usuarioBO,
                    ConexaoFactory conexaoFactory) {
        this.missaoDAO = missaoDAO;
        this.missaoUsuarioDAO = missaoUsuarioDAO;
        this.usuarioDAO = usuarioDAO;
        this.usuarioBO = usuarioBO;
        this.conexaoFactory = conexaoFactory;
    }

    public List<Missao> listar() throws SQLException {
        return missaoDAO.listar();
    }

    public Missao buscar(String nome) throws SQLException {
        if (vazio(nome)) {
            throw new RegraNegocioException("O nome da missão é obrigatório.");
        }
        return missaoDAO.buscarPorNome(nome.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Missão não encontrada."));
    }

    public Missao cadastrar(Missao missao) throws SQLException {
        validar(missao);
        missao.setNome(missao.getNome().trim());

        if (missaoDAO.buscarPorNome(missao.getNome()).isPresent()) {
            throw new ConflitoException("Missão já cadastrada.");
        }

        // Uma missão nova ainda não foi concluída por ninguém.
        missao.setConclusoes(0);
        missao.setDataConclusao(null);
        missaoDAO.inserir(missao);
        return missao;
    }

    public Missao atualizar(String nome, Missao dados) throws SQLException {
        Missao existente = buscar(nome);

        validarCamposEditaveis(dados);

        // Nome é a chave; contador e data são controlados pelo sistema.
        dados.setNome(existente.getNome());
        dados.setConclusoes(existente.getConclusoes());
        dados.setDataConclusao(existente.getDataConclusao());

        missaoDAO.atualizar(dados);
        return dados;
    }

    public void excluir(String nome) throws SQLException {
        Missao existente = buscar(nome);
        missaoDAO.deletar(existente.getNome());
    }

    /**
     * Conclui a missão para o usuário. As três escritas (registro da conclusão,
     * pontos do usuário e contador da missão) acontecem na mesma transação:
     * ou todas valem, ou nenhuma.
     */
    public ConclusaoMissaoResponse concluir(String nomeMissao, String email) throws SQLException {
        Missao missao = buscar(nomeMissao);
        Usuario usuario = usuarioBO.buscar(email);
        String hoje = LocalDate.now().toString();
        int pontosTotais = 0;

        try (Connection con = conexaoFactory.conexao()) {
            con.setAutoCommit(false);

            try {
                if (missao.isDiaria()
                        && missaoUsuarioDAO.existeConclusao(con, usuario.getEmail(), missao.getNome(), hoje)) {
                    throw new ConflitoException("A missão diária já foi concluída hoje.");
                }

                missaoUsuarioDAO.inserir(con, usuario.getEmail(), missao.getNome(), hoje);
                usuarioDAO.adicionarPontos(con, usuario.getEmail(), missao.getPontos());
                missaoDAO.incrementarConclusoes(con, missao.getNome());

                pontosTotais = usuarioDAO.buscarPorEmail(con, usuario.getEmail())
                        .map(Usuario::getPontos)
                        .orElse(usuario.getPontos() + missao.getPontos());

                con.commit();
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            }
        }

        return new ConclusaoMissaoResponse("Missão concluída.", missao.getPontos(), pontosTotais);
    }

    private void validar(Missao missao) {
        if (missao == null || vazio(missao.getNome())) {
            throw new RegraNegocioException("Nome, descrição e transporte são obrigatórios.");
        }
        validarCamposEditaveis(missao);
    }

    private void validarCamposEditaveis(Missao missao) {
        if (missao == null || vazio(missao.getDescricao()) || vazio(missao.getTransporte())) {
            throw new RegraNegocioException("Nome, descrição e transporte são obrigatórios.");
        }
        if (missao.getPontos() < 0 || missao.getCo2() < 0) {
            throw new RegraNegocioException("Pontos e CO2 não podem ser negativos.");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
