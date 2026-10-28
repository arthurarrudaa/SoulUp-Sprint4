package br.com.fiap.bo;
import br.com.fiap.entities.*;
import org.springframework.stereotype.Component;
@Component
public class ValidacaoBO {
 public void validarUsuario(Usuario u) { if (u == null || vazio(u.getNome()) || vazio(u.getEmail()) || vazio(u.getSenha())) throw new IllegalArgumentException("Nome, e-mail e senha são obrigatórios."); if (!u.getEmail().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw new IllegalArgumentException("E-mail inválido."); if (u.getPontos() < 0) throw new IllegalArgumentException("Pontos não podem ser negativos."); }
 public void validarMissao(Missao m) { if (m == null || vazio(m.getNome()) || vazio(m.getDescricao()) || vazio(m.getTransporte())) throw new IllegalArgumentException("Nome, descrição e transporte são obrigatórios."); if (m.getPontos() < 0 || m.getCo2() < 0) throw new IllegalArgumentException("Pontos e CO2 não podem ser negativos."); }
 public void validarVoucher(Voucher v) { if (v == null || vazio(v.getDescricao())) throw new IllegalArgumentException("Descrição do voucher é obrigatória."); if (v.getPontos() <= 0 || v.getValor() < 0) throw new IllegalArgumentException("Pontos devem ser positivos e valor não pode ser negativo."); }
 private boolean vazio(String s) { return s == null || s.isBlank(); }
}
