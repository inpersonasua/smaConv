package smaConv.anki_db;

import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class ColTable {
  private static final String tableStatement = "CREATE TABLE col (" + "id integer primary key,"
      + "crt integer not null," + "mod integer not null," + "scm integer not null,"
      + "ver integer not null," + "dty integer not null," + "usn integer not null,"
      + "ls integer not null," + "conf text not null," + "models text not null,"
      + "decks text not null," + "dconf text not null," + "tags text not null);";

  private final long mid;
  private final long did;
  private final long mod;

  private LinkedHashSet<String> keys;

  private Connection connection;

  public ColTable(long mid, long did, long mod) {
    this.mid = mid;
    this.did = did;
    this.mod = mod;
  }

  public void setUpTable(Connection connection) throws SQLException {
    this.connection = connection;
    PreparedStatement statement = connection.prepareStatement(tableStatement);
    statement.executeUpdate();
    statement.close();
  }

  public void insertData(String questionTemplate, String answerTemplate) throws SQLException {
    PreparedStatement colStatement = connection
        .prepareStatement("INSERT INTO col VALUES (1, 1332961200, 1398130163295, 1398130163168,"
            + " 11, 0, 0, 0, ?, ?, ?, ?, '{}')");

    colStatement.setString(1, conf());
    colStatement.setString(2, models(questionTemplate, answerTemplate));
    colStatement.setString(3, decks());
    colStatement.setString(4, dconf());
    colStatement.executeUpdate();
  }

  private String dconf() {
    String dconfTemplate = "{\"1\": {\"name\": \"Default\", \"replayq\": true, \"lapse\": {\"leechFails\": 8,"
        + " \"minInt\": 1, \"delays\": [10], \"leechAction\": 0, \"mult\": 0}, \"rev\": {\"perDay\": 100, \"ivlFct\": 1,"
        + " \"maxIvl\": 36500, \"minSpace\": 1, \"ease4\": 1.3, \"bury\": true, \"fuzz\": 0.05}, \"timer\": 0, \"maxTaken\":"
        + " 60, \"usn\": 0, \"new\": {\"separate\": true, \"delays\": [1, 10], \"perDay\": 20, \"ints\": [1, 4, 7],"
        + " \"initialFactor\": 2500, \"bury\": true, \"order\": 1}, \"autoplay\": true, \"id\": 1, \"mod\": 0}}";
    return dconfTemplate;
  }

  private String decks() {
    String decksTemplate = "{\"1\": {\"name\": \"default\", \"extendRev\": 50, \"usn\": 0, \"collapsed\":"
        + " false, \"newToday\": [0, 0], \"timeToday\": [0, 0], \"dyn\": 0, \"extendNew\": 10, \"conf\": 1, \"revToday\":"
        + " [0, 0], \"lrnToday\": [0, 0], \"id\": 1, \"mod\": 1457542003, \"desc\": \"\"}}";
    return decksTemplate;
  }

  private String conf() {
    long curModel = System.currentTimeMillis();
    String confTemplate = "{\"nextPos\": 1, \"estTimes\": true, \"activeDecks\": [1], \"sortType\": "
        + "\"noteFld\", \"timeLim\": 0, \"sortBackwards\": false, \"addToCur\": true, \"curDeck\": 1, \"newBury\":"
        + " true, \"newSpread\": 0, \"dueCounts\": true, \"curModel\": \"" + curModel
        + "\", \"collapseTime\": 1200}";
    return confTemplate;
  }

  private String models(String questionTemplate, String answerTemplate) {
    String modelsTemplate = "{\"" + mid + "\": {\"vers\": [], \"name\": \"sm\", \"tags\": [], "
        + "\"did\": " + did
        + ", \"usn\": 1586, \"req\": [[0, \"all\", [0]]], \"flds\": [], \"sortf\": 0, "
        + "\"tmpls\": [], \"type\": 0, \"id\": \"" + mid + "\", "
        + "\"css\": \".card {\\n font-family: arial;\\n font-size: 20px;\\n text-align: center;\\n color: black;\\n background-color: white;\\n}\\n\", "
        + "\"mod\": " + mod + "}}";

    String fldsTemplate = "{\"name\": \"\", \"rtl\": false, \"sticky\": false, \"media\": [], \"ord\": 0, \"font\": \"Arial\", \"size\": 20}";

    try {
      makeKeys(questionTemplate, answerTemplate);

      JsonNode fldsNode;
      JsonNode modelsNode = new ObjectMapper().readTree(new StringReader(modelsTemplate));
      int iterator = 0;
      for (String key : keys) {
        fldsNode = new ObjectMapper().readValue(fldsTemplate, JsonNode.class);
        ((ArrayNode) modelsNode.path(Long.toString(mid)).path("flds")).insert(iterator,
            ((ObjectNode) fldsNode).put("name", key).put("ord", iterator));
        iterator++;
      }

      ((ArrayNode) modelsNode.path(Long.toString(mid)).path("tmpls")).insert(0,
          makeTmpls(questionTemplate, answerTemplate));
      return modelsNode.toString();
    } catch (IOException exception) {
      throw new RuntimeException("Error at colTable: " + exception.getMessage());
    }
  }

  private void makeKeys(String questionTemplate, String answerTemplate) {
    keys = new LinkedHashSet<>();

    Pattern pattern = Pattern.compile("\\{\\{(.*?)\\}\\}");
    Matcher matcher = pattern.matcher(questionTemplate + answerTemplate);
    while (matcher.find()) {
      keys.add(matcher.group(1).replace("cloze:", ""));
    }
  }

  private JsonNode makeTmpls(String questionTemplate, String answerTemplate) throws IOException {
    String tmplsTemplate = "{\"name\": \"sm\", \"qfmt\": \"\", \"did\": null, \"bafmt\": \"\", \"afmt\": \"\", \"ord\": 0, \"bqfmt\": \"\"}";
    JsonNode tmplsNode = new ObjectMapper().readValue(tmplsTemplate, JsonNode.class);
    ((ObjectNode) tmplsNode).put("qfmt", questionTemplate);
    ((ObjectNode) tmplsNode).put("afmt", answerTemplate);
    return tmplsNode;
  }

  public LinkedHashSet<String> getKeys() {
    return keys;
  }
}
