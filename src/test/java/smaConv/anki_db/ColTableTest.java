package smaConv.anki_db;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.ArrayValueMatcher;
import org.skyscreamer.jsonassert.Customization;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.comparator.CustomComparator;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;
import org.skyscreamer.jsonassert.comparator.JSONComparator;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ColTableTest {
  ColTable colTable;
  long mid = 1485021428123L;
  long did = 1485021428124L;
  long mod = 1485021428125L;
  String questionTemplate = "{{cloze:question}}<br>{{hint}}";
  String answerTemplate = "{{answer}}{{sound}}<span class=style>{{whatever}}</span>";
  String cssStyle = ".card { }";
  Connection connection;
  ResultSet resultSet;

  @Before
  public void setUp() throws SQLException, JsonProcessingException, IOException {
    colTable = new ColTable(mid, did, mod);
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    colTable.setUpTable(connection);
    colTable.insertData(questionTemplate, answerTemplate, cssStyle);
    Statement statement = connection.createStatement();
    resultSet = statement.executeQuery("select * from col");
  }

  @Test
  public void checkOrderOfFldsNodes() throws Exception {
    String modelsValue = resultSet.getString("models");
    JSONObject jsonObject = new JSONObject(modelsValue);
    JSONArray flds = jsonObject.getJSONObject(Long.toString(mid)).getJSONArray("flds");

    JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
    Customization customization = new Customization("", new ArrayValueMatcher<>(comparator));
    JSONAssert.assertEquals(
        "[{\"name\":\"question\",\"ord\":0}, {\"name\":\"hint\",\"ord\":1},"
            + " {\"name\":\"answer\",\"ord\":2}, {\"name\":\"sound\",\"ord\":3},"
            + " {\"name\":\"whatever\",\"ord\":4}]",
        flds.toString(), new CustomComparator(JSONCompareMode.LENIENT, customization));
  }

  @Test
  public void checkTmplsArray() throws Exception {
    String modelsValue = resultSet.getString("models");
    JSONObject jsonObject = new JSONObject(modelsValue);
    JSONArray tmpls = jsonObject.getJSONObject(Long.toString(mid)).getJSONArray("tmpls");

    JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
    Customization customization = new Customization("", new ArrayValueMatcher<>(comparator));
    JSONAssert.assertEquals(
        "[{\"qfmt\":\"" + questionTemplate + "\", \"afmt\":\"" + answerTemplate + "\"}]",
        tmpls.toString(), new CustomComparator(JSONCompareMode.LENIENT, customization));
  }

  @Test
  public void checkCssStyle() throws Exception {
    String modelsValue = resultSet.getString("models");
    JSONObject jsonObject = new JSONObject(modelsValue);
    String css = jsonObject.getJSONObject(Long.toString(mid)).getString("css");

    assertThat(css).isEqualToIgnoringWhitespace(cssStyle);
  }
}
