package br.com.fiap.dto;

import br.com.fiap.entities.Voucher;

public record ResgateVoucherResponse(String mensagem, Voucher voucher, int pontosRestantes) {
}
