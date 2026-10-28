package br.com.fiap.resource;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Usuario;
import br.com.fiap.bo.ValidacaoBO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/usuarios")
public class UsuarioResource {
 private final ValidacaoBO validacao;
 public UsuarioResource(ValidacaoBO validacao) { this.validacao = validacao; }
 @GetMapping public ResponseEntity<?> listar() { try { return ResponseEntity.ok(new UsuarioDAO().listar()); } catch(Exception e) { return erro(e); } }
 @GetMapping("/{email}") public ResponseEntity<?> buscar(@PathVariable String email) { try { return new UsuarioDAO().listar().stream().filter(u -> u.getEmail().equalsIgnoreCase(email)).findFirst().<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()); } catch(Exception e) { return erro(e); } }
 @PostMapping public ResponseEntity<?> criar(@RequestBody Usuario u) { try { validacao.validarUsuario(u); if (existe(u.getEmail())) return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erro","E-mail já cadastrado.")); u.setPontos(Math.max(0,u.getPontos())); new UsuarioDAO().inserir(u); return ResponseEntity.status(HttpStatus.CREATED).body(u); } catch(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage())); } catch(Exception e) { return erro(e); } }
 @PutMapping("/{email}") public ResponseEntity<?> atualizar(@PathVariable String email,@RequestBody Usuario u) { try { if(!existe(email)) return ResponseEntity.notFound().build(); u.setEmail(email); validacao.validarUsuario(u); new UsuarioDAO().atualizar(u); return ResponseEntity.ok(u); } catch(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage())); } catch(Exception e) { return erro(e); } }
 @DeleteMapping("/{email}") public ResponseEntity<?> excluir(@PathVariable String email) { try { if(!existe(email)) return ResponseEntity.notFound().build(); new UsuarioDAO().deletar(email); return ResponseEntity.noContent().build(); } catch(Exception e) { return erro(e); } }

 @PostMapping("/login")
 public ResponseEntity<?> login(@RequestBody Map<String, String> dados) {
  try {
   String email = dados.get("email");
   String senha = dados.get("senha");

   if (email == null || email.isBlank()
           || senha == null || senha.isBlank()) {
    return ResponseEntity.badRequest()
            .body(Map.of("erro", "E-mail e senha são obrigatórios."));
   }

   Usuario usuario = new UsuarioDAO().buscarPorEmail(email);

   if (usuario != null && usuario.getSenha().equals(senha)) {
    return ResponseEntity.ok(usuario);
   }

   return ResponseEntity.status(401)
           .body(Map.of("erro", "E-mail ou senha inválidos."));

  } catch (Exception e) {
   return erro(e);


  }
 }
 private boolean existe(String email) throws Exception { return new UsuarioDAO().listar().stream().anyMatch(u -> u.getEmail().equalsIgnoreCase(email)); }
 private ResponseEntity<?> erro(Exception e) { return ResponseEntity.status(500).body(Map.of("erro","Não foi possível processar a solicitação.")); }
}
