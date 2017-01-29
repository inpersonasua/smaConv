package smaConv.anki_db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class RevlogTable {
  private static final String tableStatement = "CREATE TABLE revlog (" + "id integer primary key,"
      + "cid integer not null," + "usn integer not null," + "ease integer not null,"
      + "ivl integer not null," + "lastIvl integer not null," + "factor integer not null,"
      + "time integer not null," + "type integer not null);";

  public void setUpTable(Connection connection) throws SQLException {
    Statement statement = connection.createStatement();
    statement.executeUpdate(tableStatement);
    statement.close();
  }
}
