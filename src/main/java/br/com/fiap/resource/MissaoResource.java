package br.com.fiap.resource;
import br.com.fiap.dao.MissaoDAO;
import br.com.fiap.entities.Missao;
import br.com.fiap.bo.ValidacaoBO;
import br.com.fiap.conexao.ConexaoFactory;
import br.com.fiap.dao.UsuarioDAO;
import br.com.fiap.entities.Usuario;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;
@RestController @RequestMapping("/api/missoes")
public class MissaoResource {
 private final ValidacaoBO validacao;
 public MissaoResource(ValidacaoBO validacao) { this.validacao=validacao; }
 @GetMapping public ResponseEntity<?> listar() { try { return ResponseEntity.ok(new MissaoDAO().listar()); } catch(Exception e) { return erro(); } }
 @GetMapping("/{nome}") public ResponseEntity<?> buscar(@PathVariable String nome) { try { return new MissaoDAO().listar().stream().filter(m->m.getNome().equalsIgnoreCase(nome)).findFirst().<ResponseEntity<?>>map(ResponseEntity::ok).orElseGet(()->ResponseEntity.notFound().build()); } catch(Exception e) { return erro(); } }
 @PostMapping public ResponseEntity<?> criar(@RequestBody Missao m) { try { validacao.validarMissao(m); if(existe(m.getNome())) return ResponseEntity.status(409).body(Map.of("erro","Missão já cadastrada.")); new MissaoDAO().inserir(m); return ResponseEntity.status(201).body(m); } catch(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage())); } catch(Exception e) { return erro(); } }
 @PutMapping("/{nome}") public ResponseEntity<?> atualizar(@PathVariable String nome,@RequestBody Missao m) { try { if(!existe(nome)) return ResponseEntity.notFound().build(); m.setNome(nome); validacao.validarMissao(m); new MissaoDAO().atualizar(m); return ResponseEntity.ok(m); } catch(IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erro",e.getMessage())); } catch(Exception e) { return erro(); } }
 @DeleteMapping("/{nome}") public ResponseEntity<?> excluir(@PathVariable String nome) { try { if(!existe(nome)) return ResponseEntity.notFound().build(); new MissaoDAO().deletar(nome); return ResponseEntity.noContent().build(); } catch(Exception e) { return erro(); } }
 @PostMapping("/{nome}/conclusoes") public ResponseEntity<?> concluir(@PathVariable String nome,@RequestParam String email) { try {
  Missao m=new MissaoDAO().listar().stream().filter(x->x.getNome().equalsIgnoreCase(nome)).findFirst().orElse(null); if(m==null) return ResponseEntity.notFound().build();
  Usuario u=new UsuarioDAO().listar().stream().filter(x->x.getEmail().equalsIgnoreCase(email)).findFirst().orElse(null); if(u==null) return ResponseEntity.status(404).body(Map.of("erro","Usuário não encontrado."));
  String hoje=LocalDate.now().toString();
  try(Connection c=new ConexaoFactory().conexao()) {
   if(m.isDiaria()) try(PreparedStatement p=c.prepareStatement("SELECT COUNT(*) FROM MISSAO_USUARIO WHERE EMAIL = ? AND NOME_MISSAO = ? AND DATA_CONCLUSAO = ?")){p.setString(1,email);p.setString(2,m.getNome());p.setString(3,hoje);try(ResultSet r=p.executeQuery()){r.next();if(r.getInt(1)>0)return ResponseEntity.status(409).body(Map.of("erro","A missão diária já foi concluída hoje."));}}
   try(PreparedStatement p=c.prepareStatement("INSERT INTO MISSAO_USUARIO (EMAIL, NOME_MISSAO, DATA_CONCLUSAO) VALUES (?, ?, ?)")){p.setString(1,email);p.setString(2,m.getNome());p.setString(3,hoje);p.executeUpdate();}
  }
  u.adicionarPontos(m.getPontos()); new UsuarioDAO().atualizar(u); m.setConclusoes(m.getConclusoes()+1); new MissaoDAO().atualizar(m); return ResponseEntity.ok(Map.of("mensagem","Missão concluída.","pontosAdicionados",m.getPontos(),"pontosTotais",u.getPontos()));
 } catch(Exception e) { return erro(); } }
 private boolean existe(String n)throws Exception{return new MissaoDAO().listar().stream().anyMatch(m->m.getNome().equalsIgnoreCase(n));}
 private ResponseEntity<?> erro(){return ResponseEntity.status(500).body(Map.of("erro","Não foi possível processar a solicitação."));}
}
