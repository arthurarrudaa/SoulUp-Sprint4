package br.com.fiap.resource;

import br.com.fiap.bo.MissaoBO;
import br.com.fiap.dto.ConclusaoMissaoResponse;
import br.com.fiap.entities.Missao;
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
@RequestMapping("/api/missoes")
public class MissaoResource {

    private final MissaoBO missaoBO;

    public MissaoResource(MissaoBO missaoBO) {
        this.missaoBO = missaoBO;
    }

    @GetMapping
    public ResponseEntity<List<Missao>> listar() throws SQLException {
        return ResponseEntity.ok(missaoBO.listar());
    }

    @GetMapping("/{nome}")
    public ResponseEntity<Missao> buscar(@PathVariable String nome) throws SQLException {
        return ResponseEntity.ok(missaoBO.buscar(nome));
    }

    @PostMapping
    public ResponseEntity<Missao> cadastrar(@RequestBody Missao missao) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(missaoBO.cadastrar(missao));
    }

    @PutMapping("/{nome}")
    public ResponseEntity<Missao> atualizar(@PathVariable String nome,
                                            @RequestBody Missao missao) throws SQLException {
        return ResponseEntity.ok(missaoBO.atualizar(nome, missao));
    }

    @DeleteMapping("/{nome}")
    public ResponseEntity<Void> excluir(@PathVariable String nome) throws SQLException {
        missaoBO.excluir(nome);
        return ResponseEntity.noContent().build();
    }

    /** Cria o registro de conclusão da missão para o usuário informado. */
    @PostMapping("/{nome}/conclusoes")
    public ResponseEntity<ConclusaoMissaoResponse> concluir(@PathVariable String nome,
                                                            @RequestParam String email) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(missaoBO.concluir(nome, email));
    }
}
