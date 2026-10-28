package br.com.fiap.resource;
import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController
public class HealthResource { @GetMapping("/api/status") public Map<String,String> status(){return Map.of("aplicacao","SoulUp API","status","online");} }
