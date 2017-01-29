package smaConv.anki_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CardsTable {
  private static final String tableStatement = "CREATE TABLE cards (" + "id integer primary key,"
      + "nid integer not null," + "did integer not null," + "ord integer not null,"
      + "mod integer not null," + "usn integer not null," + "type integer not null,"
      + "queue integer not null," + "due integer not null," + "ivl integer not null,"
      + "factor integer not null," + "reps integer not null," + "lapses integer not null,"
      + "left integer not null," + "odue integer not null," + "odid integer not null,"
      + "flags integer not null," + "data text not null)";

  private final long did;
  private final long mod;

  private PreparedStatement cardsStatement;

  public CardsTable(long did, long mod) {
    this.did = did;
    this.mod = mod;
  }

  public void setUpTable(Connection connection) throws SQLException {
    PreparedStatement statement = connection.prepareStatement(tableStatement);
    statement.executeUpdate();
    cardsStatement = connection.prepareStatement(
        "INSERT INTO cards VALUES (?, ?, ?, 0, ?, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, \"\")");
  }

  public void insertDataIntoBatch(long id, long nid) throws SQLException {
    cardsStatement.setLong(1, id);
    cardsStatement.setLong(2, nid);
    cardsStatement.setLong(3, did);
    cardsStatement.setLong(4, mod);
    cardsStatement.addBatch();
  }

  public void executeBatch() throws SQLException {
    cardsStatement.executeBatch();
  }
}
