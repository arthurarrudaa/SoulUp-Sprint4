package br.com.fiap.entities;

public class Missao {
    private String nome;
    private String descricao;
    private int pontos;
    private String transporte;
    private double co2;
    private int conclusoes;
    private boolean diaria;
    private String dataConclusao;

    public Missao() {
    }

    public Missao(String nome, String descricao, int pontos,
                  String transporte, double co2) {

        this.nome = nome;
        this.descricao = descricao;
        this.pontos = pontos;
        this.transporte = transporte;
        this.co2 = co2;
        conclusoes = 0;
        diaria = false;
        dataConclusao = "";
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getTransporte() {
        return transporte;
    }

    public void setTransporte(String transporte) {
        this.transporte = transporte;
    }

    public double getCo2() {
        return co2;
    }

    public void setCo2(double co2) {
        this.co2 = co2;
    }

    public int getConclusoes() {
        return conclusoes;
    }

    public void setConclusoes(int conclusoes) {
        this.conclusoes = conclusoes;
    }

    public boolean isDiaria() {
        return diaria;
    }

    public void setDiaria(boolean diaria) {
        this.diaria = diaria;
    }

    public String getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(String dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String resumo() {
        return nome + " - " + pontos + " pontos";
    }
}
