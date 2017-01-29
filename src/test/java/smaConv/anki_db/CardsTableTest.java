package smaConv.anki_db;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import smaConv.anki_db.CardsTable;

@RunWith(value = Parameterized.class)
public class CardsTableTest {

  CardsTable cardsTable;
  static long ID = 1231231231231L;
  static long DID = 4564564564564L;
  static long NID = 7897897897897L;
  static long MOD = 1234567890123L;
  Connection connection;
  ResultSet resultSet;

  @Parameter(value = 0)
  public String valName;

  @Parameter(value = 1)
  public long expected;

  @Before
  public void setUp() throws SQLException {
    cardsTable = new CardsTable(DID, MOD);
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    cardsTable.setUpTable(connection);
    cardsTable.insertDataIntoBatch(ID, NID);
    cardsTable.executeBatch();
    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery("select * from cards");
  }

  @Parameters(name = "{index}: check value for {0}")
  public static Object[] data() {
    return new Object[][] { //
        { "id", ID }, //
        { "did", DID }, //
        { "nid", NID }, //
        { "mod", MOD }//
    };
  }

  @Test
  public void checkValues() throws Exception {
    assertThat(resultSet.getString(valName)).isEqualTo(Long.toString(expected));
  }
}
