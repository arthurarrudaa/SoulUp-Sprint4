package br.com.fiap.resource;

import br.com.fiap.bo.UsuarioBO;
import br.com.fiap.dto.LoginRequest;
import br.com.fiap.entities.Usuario;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

    private final UsuarioBO usuarioBO;

    public UsuarioResource(UsuarioBO usuarioBO) {
        this.usuarioBO = usuarioBO;
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> listar() throws SQLException {
        return ResponseEntity.ok(usuarioBO.listar());
    }

    @GetMapping("/{email}")
    public ResponseEntity<Usuario> buscar(@PathVariable String email) throws SQLException {
        return ResponseEntity.ok(usuarioBO.buscar(email));
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioBO.cadastrar(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest dados) throws SQLException {
        return ResponseEntity.ok(usuarioBO.autenticar(dados.email(), dados.senha()));
    }

    @PutMapping("/{email}")
    public ResponseEntity<Usuario> atualizar(@PathVariable String email,
                                             @RequestBody Usuario usuario) throws SQLException {
        return ResponseEntity.ok(usuarioBO.atualizar(email, usuario));
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<Void> excluir(@PathVariable String email) throws SQLException {
        usuarioBO.excluir(email);
        return ResponseEntity.noContent().build();
    }
}
