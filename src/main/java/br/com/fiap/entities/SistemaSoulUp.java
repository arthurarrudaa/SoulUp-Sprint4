package br.com.fiap.entities;

import javax.swing.*;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.dao.HistoricoDAO;
import br.com.fiap.dao.VoucherDAO;
import br.com.fiap.conexao.ConexaoFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class SistemaSoulUp {
    private Usuario usuario;
    private ArrayList<Missao> missoes;
    private ArrayList<Historico> historicos;

    public SistemaSoulUp() {
        missoes = new ArrayList<>();
        historicos = new ArrayList<>();
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public ArrayList<Missao> getMissoes() {
        return missoes;
    }

    public ArrayList<Historico> getHistoricos() {
        return historicos;
    }

    public void cadastrarUsuario() {

        String nome = JOptionPane.showInputDialog(
                "=== CADASTRO ===\n\nDigite seu nome:");

        while (nome == null || nome.trim().isEmpty()) {

            if (nome == null) {
                System.exit(0);
            }

            JOptionPane.showMessageDialog(null,
                    "Digite seu nome para prosseguir.");

            nome = JOptionPane.showInputDialog(
                    "=== CADASTRO ===\n\nDigite seu nome:");
        }

        String email = JOptionPane.showInputDialog(
                "=== CADASTRO ===\n\nDigite seu email:");

        while (email == null || email.trim().isEmpty()) {

            if (email == null) {
                System.exit(0);
            }

            JOptionPane.showMessageDialog(null,
                    "Digite seu email para prosseguir.");

            email = JOptionPane.showInputDialog(
                    "=== CADASTRO ===\n\nDigite seu email:");
        }

        try {

            UsuarioDAO dao = new UsuarioDAO();

            ArrayList<Usuario> usuarios = dao.listar();

            for (Usuario u : usuarios) {

                if (u.getEmail().equalsIgnoreCase(email)) {

                    JOptionPane.showMessageDialog(null,
                            "Esse email já está cadastrado.");

                    return;
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao verificar o email.");
            return;
        }

        String senha = JOptionPane.showInputDialog(
                "=== CADASTRO ===\n\nDigite sua senha:");

        while (senha == null || senha.trim().isEmpty()) {

            if (senha == null) {
                System.exit(0);
            }

            JOptionPane.showMessageDialog(null,
                    "Digite sua senha para prosseguir.");

            senha = JOptionPane.showInputDialog(
                    "=== CADASTRO ===\n\nDigite sua senha:");
        }

        usuario = new Usuario(nome, email, senha);

        try {

            UsuarioDAO dao = new UsuarioDAO();
            dao.inserir(usuario);

            JOptionPane.showMessageDialog(null,
                    "Cadastro realizado com sucesso!");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao cadastrar usuário.");
        }
    }

    public void login() {

        while (true) {

            String email = JOptionPane.showInputDialog(
                    "=== LOGIN ===\n\nDigite seu email:");

            if (email == null) {
                System.exit(0);
            }

            String senha = JOptionPane.showInputDialog(
                    "=== LOGIN ===\n\nDigite sua senha:");

            if (senha == null) {
                System.exit(0);
            }

            try {

                UsuarioDAO dao = new UsuarioDAO();

                ArrayList<Usuario> usuarios = dao.listar();

                for (Usuario usuarioBanco : usuarios) {

                    boolean emailCorreto =
                            email.equalsIgnoreCase(usuarioBanco.getEmail());

                    boolean senhaCorreta =
                            senha.equals(usuarioBanco.getSenha());

                    if (emailCorreto && senhaCorreta) {

                        usuario = usuarioBanco;

                        JOptionPane.showMessageDialog(null,
                                "Login realizado com sucesso!\n\n"
                                        + "Bem-vindo, "
                                        + usuario.getNome() + "!");

                        return;
                    }
                }

                JOptionPane.showMessageDialog(null,
                        "Email ou senha incorretos.");

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null,
                        "Erro ao acessar o banco.");
            }
        }
    }

    public void criarMissoes() {

        try {

            MissaoDAO dao = new MissaoDAO();

            // Verifica se já existem missões no banco
            ArrayList<Missao> missoesBanco = dao.listar();

            if (missoesBanco.isEmpty()) {

                Missao missao1 = new Missao(
                        "Ônibus Sustentável",
                        "Use ônibus durante o dia.",
                        50,
                        "Ônibus",
                        1.5
                );

                Missao missao2 = new Missao(
                        "Metrô Inteligente",
                        "Use metrô no seu trajeto.",
                        80,
                        "Metrô",
                        2.5
                );

                Missao missao3 = new Missao(
                        "Pedalada Verde",
                        "Use bicicleta durante o dia.",
                        150,
                        "Bicicleta",
                        5
                );

                Missao missao4 = new Missao(
                        "Carona Compartilhada",
                        "Compartilhe uma carona.",
                        100,
                        "Carona",
                        3
                );

                Missao missao5 = new Missao(
                        "Caminhada Saudável",
                        "Faça um trajeto caminhando.",
                        180,
                        "Caminhada",
                        6
                );

                Missao missao6 = new Missao(
                        "Missão Diária",
                        "Use um meio de transporte sustentável hoje.",
                        200,
                        "Transporte sustentável",
                        1
                );

                missao6.setDiaria(true);

                // Cadastra no Oracle
                dao.inserir(missao1);
                dao.inserir(missao2);
                dao.inserir(missao3);
                dao.inserir(missao4);
                dao.inserir(missao5);
                dao.inserir(missao6);
            }

            // Limpa a lista que está na memória
            missoes.clear();

            // Busca as missões do Oracle
            missoes.addAll(dao.listar());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao carregar missões:\n"
                            + e.getMessage());
        }
    }

    public void verMissoes() {

        String texto = "=== MISSÕES ===\n\n";

        for (int i = 0; i < missoes.size(); i++) {
            texto += (i + 1) + " - "
                    + missoes.get(i).resumo()
                    + "\n";
        }

        JOptionPane.showMessageDialog(null, texto);
    }

    public void concluirMissao() {

        String texto = "=== CONCLUIR MISSÃO ===\n\n";

        for (int i = 0; i < missoes.size(); i++) {
            texto += (i + 1) + " - "
                    + missoes.get(i).getNome()
                    + "\n";
        }

        texto += "\n0 - Voltar";

        try {

            int opcao = Integer.parseInt(
                    JOptionPane.showInputDialog(texto));

            if (opcao == 0) {
                return;
            }

            if (opcao < 1 || opcao > missoes.size()) {
                JOptionPane.showMessageDialog(null,
                        "Opção inválida.");
                return;
            }

            Missao missao = missoes.get(opcao - 1);

            String hoje = LocalDate.now().toString();

            // Verifica se o usuário já fez essa missão hoje
            if (missao.isDiaria()) {

                ConexaoFactory conexaoFactory = new ConexaoFactory();
                Connection conexao = conexaoFactory.conexao();

                PreparedStatement stmt = conexao.prepareStatement(
                        "SELECT COUNT(*) FROM MISSAO_USUARIO " +
                                "WHERE EMAIL = ? " +
                                "AND NOME_MISSAO = ? " +
                                "AND DATA_CONCLUSAO = ?"
                );

                stmt.setString(1, usuario.getEmail());
                stmt.setString(2, missao.getNome());
                stmt.setString(3, hoje);

                ResultSet rs = stmt.executeQuery();

                rs.next();

                int quantidade = rs.getInt(1);

                rs.close();
                stmt.close();
                conexao.close();

                if (quantidade > 0) {

                    JOptionPane.showMessageDialog(null,
                            "Você já fez a missão diária hoje.\n"
                                    + "Volte amanhã!");

                    return;
                }
            }

            // Adiciona os pontos ao usuário
            usuario.adicionarPontos(
                    missao.getPontos());

            UsuarioDAO usuarioDAO = new UsuarioDAO();
            usuarioDAO.atualizar(usuario);

            // Atualiza a quantidade de conclusões da missão
            missao.setConclusoes(
                    missao.getConclusoes() + 1);

            MissaoDAO missaoDAO = new MissaoDAO();
            missaoDAO.atualizar(missao);

            // Salva a missão realizada pelo usuário
            ConexaoFactory conexaoFactory = new ConexaoFactory();
            Connection conexao = conexaoFactory.conexao();

            PreparedStatement stmt = conexao.prepareStatement(
                    "INSERT INTO MISSAO_USUARIO " +
                            "(EMAIL, NOME_MISSAO, DATA_CONCLUSAO) " +
                            "VALUES (?, ?, ?)"
            );

            stmt.setString(1, usuario.getEmail());
            stmt.setString(2, missao.getNome());
            stmt.setString(3, hoje);

            stmt.executeUpdate();

            stmt.close();
            conexao.close();

            JOptionPane.showMessageDialog(null,
                    "Missão concluída!\n\n+"
                            + missao.getPontos()
                            + " pontos.");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao concluir missão:\n"
                            + e.getMessage());
        }
    }

    public void mostrarPontos() {

        JOptionPane.showMessageDialog(null,
                "=== SEUS PONTOS ===\n\n"
                        + "Pontos atuais: "
                        + usuario.getPontos());
    }

    public void trocarVoucher() {

        try {

            VoucherDAO dao = new VoucherDAO();

            ArrayList<Voucher> vouchers = dao.listar();

            if (vouchers.isEmpty()) {

                JOptionPane.showMessageDialog(null,
                        "Nenhum voucher disponível.");
                return;
            }

            String menu = "=== VOUCHERS ===\n\n";

            for (int i = 0; i < vouchers.size(); i++) {

                Voucher voucher = vouchers.get(i);

                menu += (i + 1) + " - "
                        + voucher.getDescricao()
                        + " | "
                        + voucher.getPontos()
                        + " pontos\n";
            }

            menu += "\n0 - Voltar";

            int opcao = Integer.parseInt(
                    JOptionPane.showInputDialog(menu));

            if (opcao == 0) {
                return;
            }

            if (opcao < 1 || opcao > vouchers.size()) {

                JOptionPane.showMessageDialog(null,
                        "Opção inválida.");
                return;
            }

            Voucher voucher = vouchers.get(opcao - 1);

            // Verifica se o usuário tem pontos
            if (usuario.getPontos() < voucher.getPontos()) {

                JOptionPane.showMessageDialog(null,
                        "Pontos insuficientes.");
                return;
            }

            Historico historico = new Historico(
                    voucher.getCodigo(),
                    voucher.getPontos(),
                    LocalDate.now().toString());

            try {

                HistoricoDAO historicoDAO = new HistoricoDAO();

                historicoDAO.inserir(
                        historico,
                        usuario.getEmail());

                usuario.removerPontos(voucher.getPontos());

                UsuarioDAO usuarioDAO = new UsuarioDAO();
                usuarioDAO.atualizar(usuario);

                historicos.add(historico);

                JOptionPane.showMessageDialog(null,
                        "Voucher resgatado!\n\n"
                                + "Código: "
                                + voucher.getCodigo()
                                + "\nValor: R$ "
                                + String.format("%.2f",
                                voucher.getValor()));

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null,
                        "Não foi possível resgatar o voucher.\n"
                                + "Seus pontos não foram descontados.");
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Digite apenas números.");
        }
    }

    public void verHistorico() {

        try {

            HistoricoDAO dao = new HistoricoDAO();

            historicos = dao.listar(
                    usuario.getEmail());

            if (historicos.isEmpty()) {

                JOptionPane.showMessageDialog(null,
                        "Nenhum voucher resgatado.");
                return;
            }

            String texto = "=== HISTÓRICO ===\n\n";

            for (Historico h : historicos) {

                texto += h.exibirHistorico()
                        + "\n\n";
            }

            JOptionPane.showMessageDialog(null, texto);

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao carregar histórico:\n"
                            + e.getMessage());
        }
    }

    public void rankingEcologico() {

        int pontos = usuario.getPontos();
        int missoesFeitas = 0;
        double co2 = 0;

        try {

            ConexaoFactory conexaoFactory = new ConexaoFactory();
            Connection conexao = conexaoFactory.conexao();

            PreparedStatement stmt = conexao.prepareStatement(
                    "SELECT NOME_MISSAO FROM MISSAO_USUARIO WHERE EMAIL = ?"
            );

            stmt.setString(1, usuario.getEmail());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                String nomeMissao = rs.getString("NOME_MISSAO");

                for (Missao missao : missoes) {

                    if (missao.getNome().equals(nomeMissao)) {

                        missoesFeitas++;

                        co2 += missao.getCo2();

                        break;
                    }
                }
            }

            rs.close();
            stmt.close();
            conexao.close();

            String nivel;

            if (pontos < 100) {
                nivel = "Iniciante Sustentável";
            } else if (pontos < 300) {
                nivel = "Explorador Verde";
            } else if (pontos < 600) {
                nivel = "Guardião Ecológico";
            } else {
                nivel = "Mestre da Sustentabilidade";
            }

            JOptionPane.showMessageDialog(null,
                    "=== SUA TRAJETÓRIA ===\n\n"
                            + "Olá, "
                            + usuario.getNome()
                            + "!"
                            + "\n\n"
                            + "Esta é sua trajetória até o momento."
                            + "\n\n"
                            + "Nível: " + nivel
                            + "\nPontos: " + pontos
                            + "\nMissões concluídas: "
                            + missoesFeitas
                            + "\nCO₂ economizado: "
                            + String.format("%.1f", co2)
                            + " kg\n\n"
                            + "Continue sua jornada sustentável!");

        } catch (Exception e) {

            JOptionPane.showMessageDialog(null,
                    "Erro ao carregar ranking.");
        }
    }
}