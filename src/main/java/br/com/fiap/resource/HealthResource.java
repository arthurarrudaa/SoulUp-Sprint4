package br.com.fiap.resource;

import br.com.fiap.conexao.ConexaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

@RestController
@RequestMapping("/api/status")
public class HealthResource {

    private static final Logger log = LoggerFactory.getLogger(HealthResource.class);

    private final ConexaoFactory conexaoFactory;

    public HealthResource(ConexaoFactory conexaoFactory) {
        this.conexaoFactory = conexaoFactory;
    }

    /** Mostra apenas que a aplicação está no ar. */
    @GetMapping
    public Map<String, String> status() {
        return Map.of("aplicacao", "SoulUp API", "status", "online");
    }

    /** Confirma também que o banco de dados responde (útil para validar o deploy). */
    @GetMapping("/banco")
    public ResponseEntity<Map<String, String>> statusBanco() {
        try (Connection con = conexaoFactory.conexao();
             PreparedStatement stmt = con.prepareStatement("SELECT 1 FROM DUAL");
             ResultSet rs = stmt.executeQuery()) {

            rs.next();
            return ResponseEntity.ok(Map.of("aplicacao", "SoulUp API", "banco", "online"));

        } catch (Exception e) {
            log.error("Falha ao consultar o banco de dados", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("aplicacao", "SoulUp API", "banco", "indisponível"));
        }
    }
}
