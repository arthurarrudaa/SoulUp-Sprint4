
package br.com.fiap.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexaoFactory {

 public Connection conexao() throws SQLException {
  String url = System.getenv("SOULUP_DB_URL");
  String usuario = System.getenv("SOULUP_DB_USER");
  String senha = System.getenv("SOULUP_DB_PASSWORD");

  if (url == null || url.isBlank()) {
   url = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:orcl";
  }

  if (usuario == null || usuario.isBlank()
          || senha == null || senha.isBlank()) {
   throw new SQLException(
           "Configure SOULUP_DB_USER e SOULUP_DB_PASSWORD."
   );
  }

  return DriverManager.getConnection(url, usuario, senha);
 }
}
