package br.com.fiap.main;

import br.com.fiap.entities.SistemaSoulUp;
import javax.swing.JOptionPane;

public class Main {

    public static void main(String[] args) {

        SistemaSoulUp sistema = new SistemaSoulUp();

        sistema.criarMissoes();

        while (true) {

            String menu =
                    "=== ECO MOBILITY SOULUP ===\n\n"
                            + "1 - Cadastrar\n"
                            + "2 - Login\n"
                            + "0 - Sair\n\n"
                            + "Escolha uma opção:";

            try {

                int opcao = Integer.parseInt(
                        JOptionPane.showInputDialog(menu));

                if (opcao == 0) {

                    JOptionPane.showMessageDialog(null,
                            "Obrigado pela visita ao Eco Mobility da SoulUp!");

                    System.exit(0);
                }

                if (opcao == 1) {
                    sistema.cadastrarUsuario();
                }

                if (opcao == 2) {
                    sistema.login();
                    menuPrincipal(sistema);
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null,
                        "Digite apenas números.");
            }
        }
    }

    public static void menuPrincipal(SistemaSoulUp sistema) {

        while (true) {

            try {

                String menu =
                        "=== ECO MOBILITY SOULUP ===\n\n"
                                + "1 - Ver missões\n"
                                + "2 - Concluir missão\n"
                                + "3 - Ver pontos\n"
                                + "4 - Trocar voucher\n"
                                + "5 - Ver histórico\n"
                                + "6 - Ver ranking ecológico\n"
                                + "7 - Sair\n\n"
                                + "Escolha uma opção:";

                int opcao = Integer.parseInt(
                        JOptionPane.showInputDialog(menu));

                switch (opcao) {

                    case 1:
                        sistema.verMissoes();
                        break;

                    case 2:
                        sistema.concluirMissao();
                        break;

                    case 3:
                        sistema.mostrarPontos();
                        break;

                    case 4:
                        sistema.trocarVoucher();
                        break;

                    case 5:
                        sistema.verHistorico();
                        break;

                    case 6:
                        sistema.rankingEcologico();
                        break;

                    case 7:
                        JOptionPane.showMessageDialog(null,
                                "Obrigado pela visita ao Eco Mobility da SoulUp!");
                        System.exit(0);
                        break;

                    default:
                        JOptionPane.showMessageDialog(null,
                                "Escolha uma opção válida.");
                }

            } catch (Exception e) {

                JOptionPane.showMessageDialog(null,
                        "Digite apenas números.");
            }
        }
    }
}


























