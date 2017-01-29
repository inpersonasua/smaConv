package smaConv.anki_db;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import smaConv.anki_db.NotesTable;
import smaConv.util.AnkiCard;

@RunWith(value = Parameterized.class)
public class NotesTableTest {

  NotesTable notesTable;
  static final long MID = 4564564564564L;
  static final long NID = 7897897897897L;
  static final long MOD = 1234567890123L;
  ResultSet resultSet;
  AnkiCard card;

  @Before
  public void setUp() throws SQLException {

    Map<String, String> question = new HashMap<>();
    question.put("front", "Lorem ipsum...");
    Map<String, String> answer = new HashMap<>();
    answer.put("back", "...dolor sit amet");
    card = new AnkiCard(question, answer);
    LinkedHashSet<String> keys = new LinkedHashSet<>(Arrays.asList("front", "back"));

    notesTable = new NotesTable(MID, MOD);
    Connection connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    notesTable.setUpTable(connection);
    notesTable.setKeys(keys);
    notesTable.insertDataIntoBatch(card, NID);
    notesTable.executeBatch();
    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery("select * from notes");
  }

  @Parameter(value = 0)
  public String valName;

  @Parameter(value = 1)
  public String expected;

  @Parameters(name = "{index}: check value for {0}")
  public static Object[] data() {
    return new Object[][] { //
        { "id", Long.toString(NID) }, //
        { "mid", Long.toString(MID) }, //
        { "mod", Long.toString(MOD) }, //
        { "flds", "Lorem ipsum..." + (char) 0x1F + "...dolor sit amet" }, //
        { "sfld", "Lorem ipsum..." }, //
        { "csum", "4036353563" }, //
    };
  }

  @Test
  public void checkValues() throws Exception {
    assertThat(resultSet.getString(valName)).isEqualTo(expected);
  }

}
