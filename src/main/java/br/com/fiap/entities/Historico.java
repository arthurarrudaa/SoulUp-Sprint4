package br.com.fiap.entities;

public class Historico {
    private String codigo;
    private int pontos;
    private String data;

    public Historico() {
    }

    public Historico(String codigo, int pontos, String data) {
        this.codigo = codigo;
        this.pontos = pontos;
        this.data = data;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String exibirHistorico() {
        return "Código: " + codigo
                + "\nPontos utilizados: " + pontos
                + "\nData: " + data;
    }
}
