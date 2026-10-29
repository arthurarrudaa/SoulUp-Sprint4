package br.com.fiap.resource;

import br.com.fiap.bo.HistoricoBO;
import br.com.fiap.entities.Historico;
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
@RequestMapping("/api/historicos")
public class HistoricoResource {

    private final HistoricoBO historicoBO;

    public HistoricoResource(HistoricoBO historicoBO) {
        this.historicoBO = historicoBO;
    }

    @GetMapping
    public ResponseEntity<List<Historico>> listar(@RequestParam String email) throws SQLException {
        return ResponseEntity.ok(historicoBO.listar(email));
    }

    @PostMapping
    public ResponseEntity<Historico> registrar(@RequestParam String email,
                                               @RequestBody Historico historico) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(historicoBO.registrar(email, historico));
    }

    @PutMapping("/{codigo}")
    public ResponseEntity<Historico> atualizar(@PathVariable String codigo,
                                               @RequestParam String email,
                                               @RequestBody Historico historico) throws SQLException {
        return ResponseEntity.ok(historicoBO.atualizar(codigo, email, historico));
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<Void> excluir(@PathVariable String codigo,
                                        @RequestParam String email) throws SQLException {
        historicoBO.excluir(codigo, email);
        return ResponseEntity.noContent().build();
    }
}
