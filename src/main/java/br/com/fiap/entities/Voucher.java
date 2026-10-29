package br.com.fiap.entities;

public class Voucher {

    private String codigo;
    private String descricao;
    private int pontos;
    private double valor;

    public Voucher() {
    }

    public Voucher(String codigo, String descricao, int pontos, double valor) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.pontos = pontos;
        this.valor = valor;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }
}
