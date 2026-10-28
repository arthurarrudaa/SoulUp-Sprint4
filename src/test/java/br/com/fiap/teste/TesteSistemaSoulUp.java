package br.com.fiap.teste;

import br.com.fiap.entities.Usuario;

public class TesteSistemaSoulUp {

    public static void main(String[] args) {

        System.out.println("=== TESTE DO SISTEMA SOULUP ===");

        Usuario usuario = new Usuario(
                "Arthur",
                "arthur@email.com",
                "123456"
        );

        System.out.println("\nUsuário criado:");
        System.out.println("Nome: " + usuario.getNome());
        System.out.println("E-mail: " + usuario.getEmail());
        System.out.println("Pontos iniciais: " + usuario.getPontos());

        usuario.adicionarPontos(100);

        System.out.println("Pontos após adicionar 100: "
                + usuario.getPontos());

        boolean removeu = usuario.removerPontos(30);

        System.out.println("Removeu 30 pontos? " + removeu);
        System.out.println("Pontos atuais: " + usuario.getPontos());

        System.out.println("\n=== TESTE FINALIZADO ===");
    }
}