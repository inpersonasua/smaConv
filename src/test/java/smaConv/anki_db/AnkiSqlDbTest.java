package smaConv.anki_db;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import smaConv.anki_db.AnkiSqlDb;
import smaConv.util.AnkiCard;
import smaConv.util.Deck;

@RunWith(Suite.class)
@SuiteClasses({ AnkiSqlDbTest.ParameterizedTest.class, AnkiSqlDbTest.NotParameterizedTest.class })
public class AnkiSqlDbTest {
  static Deck<AnkiCard> deck = new Deck<>();
  static AnkiSqlDb sqlDb;
  static Statement statement;

  @ClassRule
  public static TemporaryFolder folder = new TemporaryFolder();
  static File dbFile;

  @BeforeClass
  public static void setUp() throws Exception {
    deck.setQuestionTemplate("{front}");
    deck.setAnswerTemplate("{back}");

    Map<String, String> question = new HashMap<>();
    question.put("front", "value for front");
    Map<String, String> answer = new HashMap<>();
    answer.put("back", "value for back");

    deck.add(new AnkiCard(question, answer));
    Map<String, String> question1 = new HashMap<>();
    question.put("front", "value for front");
    Map<String, String> answer1 = new HashMap<>();
    answer.put("back", "value for back");
    deck.add(new AnkiCard(question1, answer1));

    sqlDb = new AnkiSqlDb();
    sqlDb.createDb(deck);
    dbFile = folder.newFile("temp.db");
    Files.write(dbFile.toPath(), sqlDb.getFile());

    Connection connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
    statement = connection.createStatement();
  }

  @RunWith(value = Parameterized.class)
  public static class ParameterizedTest {

    @Parameter(value = 0)
    public String sqlQuery;

    @Parameter(value = 1)
    public int rowCountForTwoCards;

    @Parameters(name = "{0}")
    public static Collection<Object[]> data() {
      return Arrays.asList(new Object[][] { //
          { "select * from cards", 2 }, //
          { "select * from col", 1, }, //
          { "select * from graves", 0 }, //
          { "select * from notes", 2 }, //
          { "select * from revlog", 0 },//
      });
    }

    @Test
    public void checkIfAllTablesAreCreated() throws Exception {
      // exception will be thrown when table doesn't exist
      assertThat(statement.executeUpdate(sqlQuery)).isEqualTo(0);
    }

    @Test
    public void shouldTwoRowsBeAdded() throws Exception {
      ResultSet rSet;
      rSet = statement.executeQuery(sqlQuery);
      int i = 0;
      while (rSet.next()) {
        i++;
      }

      assertThat(i).isEqualTo(rowCountForTwoCards);
    }
  }

  public static class NotParameterizedTest {

    @Test
    public void exceptionShouldBeThrownWhenDeckIsEmpty() {
      AnkiSqlDb db = new AnkiSqlDb();
      Deck<AnkiCard> deck = new Deck<>();
      deck.setQuestionTemplate("{{front}}");
      deck.setAnswerTemplate("{{back}}");

      assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> db.createDb(deck))
          .withMessage("Deck doesn't contain any cards.");
    }

    @Test
    public void shouldReturnBaseContent() {
      assertThat(sqlDb.getFile()).isNotEmpty();
    }

  }
}