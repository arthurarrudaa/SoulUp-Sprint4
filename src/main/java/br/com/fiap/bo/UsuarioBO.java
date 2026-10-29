package br.com.fiap.bo;

import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Usuario;
import br.com.fiap.exception.ConflitoException;
import br.com.fiap.exception.CredenciaisInvalidasException;
import br.com.fiap.exception.RecursoNaoEncontradoException;
import br.com.fiap.exception.RegraNegocioException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

@Service
public class UsuarioBO {

    private static final Pattern EMAIL_VALIDO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final UsuarioDAO usuarioDAO;

    public UsuarioBO(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public List<Usuario> listar() throws SQLException {
        return usuarioDAO.listar();
    }

    public Usuario buscar(String email) throws SQLException {
        if (vazio(email)) {
            throw new RegraNegocioException("O e-mail é obrigatório.");
        }
        return usuarioDAO.buscarPorEmail(email.trim())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    public Usuario cadastrar(Usuario usuario) throws SQLException {
        validarDadosBasicos(usuario);

        if (vazio(usuario.getSenha())) {
            throw new RegraNegocioException("A senha é obrigatória.");
        }

        usuario.setNome(usuario.getNome().trim());
        usuario.setEmail(usuario.getEmail().trim());

        if (usuarioDAO.buscarPorEmail(usuario.getEmail()).isPresent()) {
            throw new ConflitoException("E-mail já cadastrado.");
        }

        // Todo usuário começa sem pontos; eles só vêm de missões concluídas.
        usuario.setPontos(0);
        usuarioDAO.inserir(usuario);
        return usuario;
    }

    public Usuario atualizar(String email, Usuario dados) throws SQLException {
        Usuario existente = buscar(email);

        if (dados == null || vazio(dados.getNome())) {
            throw new RegraNegocioException("O nome é obrigatório.");
        }
        if (dados.getPontos() < 0) {
            throw new RegraNegocioException("Pontos não podem ser negativos.");
        }

        // O e-mail é a chave do usuário e não muda; senha em branco mantém a atual.
        dados.setEmail(existente.getEmail());
        dados.setNome(dados.getNome().trim());
        if (vazio(dados.getSenha())) {
            dados.setSenha(existente.getSenha());
        }

        usuarioDAO.atualizar(dados);
        return dados;
    }

    public void excluir(String email) throws SQLException {
        Usuario existente = buscar(email);
        usuarioDAO.deletar(existente.getEmail());
    }

    public Usuario autenticar(String email, String senha) throws SQLException {
        if (vazio(email) || vazio(senha)) {
            throw new RegraNegocioException("E-mail e senha são obrigatórios.");
        }

        Usuario usuario = usuarioDAO.buscarPorEmail(email.trim())
                .orElseThrow(() -> new CredenciaisInvalidasException("E-mail ou senha inválidos."));

        if (!senha.equals(usuario.getSenha())) {
            throw new CredenciaisInvalidasException("E-mail ou senha inválidos.");
        }
        return usuario;
    }

    private void validarDadosBasicos(Usuario usuario) {
        if (usuario == null || vazio(usuario.getNome()) || vazio(usuario.getEmail())) {
            throw new RegraNegocioException("Nome, e-mail e senha são obrigatórios.");
        }
        if (!EMAIL_VALIDO.matcher(usuario.getEmail().trim()).matches()) {
            throw new RegraNegocioException("E-mail inválido.");
        }
    }

    private boolean vazio(String texto) {
        return texto == null || texto.isBlank();
    }
}
