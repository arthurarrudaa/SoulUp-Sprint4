package br.com.fiap.resource;

import br.com.fiap.bo.VoucherBO;
import br.com.fiap.dto.ResgateVoucherResponse;
import br.com.fiap.entities.Voucher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherResource {

    private final VoucherBO voucherBO;

    public VoucherResource(VoucherBO voucherBO) {
        this.voucherBO = voucherBO;
    }

    @GetMapping
    public ResponseEntity<List<Voucher>> listar() throws SQLException {
        return ResponseEntity.ok(voucherBO.listar());
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Voucher> buscar(@PathVariable String codigo) throws SQLException {
        return ResponseEntity.ok(voucherBO.buscar(codigo));
    }

    @PostMapping
    public ResponseEntity<Voucher> cadastrar(@RequestBody Voucher voucher) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherBO.cadastrar(voucher));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Voucher> atualizar(@PathVariable String codigo,
                                             @RequestBody Voucher voucher) throws SQLException {
        return ResponseEntity.ok(voucherBO.atualizar(codigo, voucher));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable String codigo) throws SQLException {
        voucherBO.excluir(codigo);
        return ResponseEntity.noContent().build();
    }

    /** Cria o resgate do voucher para o usuário informado (desconta pontos e grava o histórico). */
    @PostMapping("/{codigo}/resgates")
    public ResponseEntity<ResgateVoucherResponse> resgatar(@PathVariable String codigo,
                                                           @RequestParam String email) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(voucherBO.resgatar(codigo, email));
    }
}
