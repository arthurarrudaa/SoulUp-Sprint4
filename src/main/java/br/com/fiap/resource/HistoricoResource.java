package br.com.fiap.resource;
import br.com.fiap.dao.HistoricoDAO; import br.com.fiap.entities.Historico; import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.*;
@RestController @RequestMapping("/api/historicos")
public class HistoricoResource {
 @GetMapping public ResponseEntity<?> listar(@RequestParam String email){try{return ResponseEntity.ok(new HistoricoDAO().listar(email));}catch(Exception e){return erro();}}
 @PostMapping public ResponseEntity<?> criar(@RequestParam String email,@RequestBody Historico h){try{if(email.isBlank()||h.getCodigo()==null||h.getCodigo().isBlank()||h.getPontos()<0)return ResponseEntity.badRequest().body(Map.of("erro","E-mail, código e pontos válidos são obrigatórios."));new HistoricoDAO().inserir(h,email);return ResponseEntity.status(201).body(h);}catch(Exception e){return erro();}}
 @PutMapping("/{codigo}") public ResponseEntity<?> atualizar(@PathVariable String codigo,@RequestParam String email,@RequestBody Historico h){try{if(!existe(codigo,email))return ResponseEntity.notFound().build();new HistoricoDAO().atualizar(h,email,codigo);return ResponseEntity.ok(h);}catch(Exception e){return erro();}}
 @DeleteMapping("/{codigo}") public ResponseEntity<?> excluir(@PathVariable String codigo,@RequestParam String email){try{if(!existe(codigo,email))return ResponseEntity.notFound().build();new HistoricoDAO().deletar(codigo,email);return ResponseEntity.noContent().build();}catch(Exception e){return erro();}}
 private boolean existe(String codigo,String email)throws Exception{return new HistoricoDAO().listar(email).stream().anyMatch(h->h.getCodigo().equalsIgnoreCase(codigo));} private ResponseEntity<?> erro(){return ResponseEntity.status(500).body(Map.of("erro","Não foi possível processar a solicitação."));}
}
