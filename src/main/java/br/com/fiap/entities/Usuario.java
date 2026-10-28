package br.com.fiap.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private int pontos;

    public Usuario() {
    }

    public Usuario(String nome, String email, String senha) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        pontos = 0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public void adicionarPontos(int pontos) {
        this.pontos += pontos;
    }

    public boolean removerPontos(int pontos) {
        if (this.pontos >= pontos) {
            this.pontos -= pontos;
            return true;
        }

        return false;
    }
}
