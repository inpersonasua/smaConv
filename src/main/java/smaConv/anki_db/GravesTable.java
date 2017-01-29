package smaConv.anki_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class GravesTable {
  private static final String tableStatement = "CREATE TABLE graves (" + "usn integer not null,"
      + "oid integer not null," + "type integer not null);";

  public void setUpTable(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(tableStatement);
    statement.executeUpdate();
    statement.close();
  }
}
