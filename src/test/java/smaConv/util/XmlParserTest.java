package smaConv.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import smaConv.util.XmlParser;

public class XmlParserTest {
  private final XmlParser xmlParser = new XmlParser();
  private final String xmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
      + "<item xmlns=\"http://www.supermemo.net/2006/smux\">\r\n"
      + "  <question>Question</question>\r\n" + "  <answer>aNsWeR</answer>\r\n" + "</item>";

  private final String courseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" + "  <title>Title</title>\r\n"
      + "  <element id=\"1\" type=\"pres\" name=\"course\" />\r\n"
      + "  <element id=\"83\" type=\"pres\" name=\"Introduction\" />\r\n"
      + "  <element id=\"2\" type=\"pres\" name=\"1 - 2600\" disabled=\"true\">\r\n"
      + "    <element id=\"103\" type=\"pres\" name=\"1 - 200\" disabled=\"true\">\r\n"
      + "      <element id=\"21222\" type=\"pres\" subtype=\"1000\" name=\"\" disabled=\"true\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\">\r\n"
      + "        <element id=\"21223\" type=\"exercise\" subtype=\"1\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"
      + "        <element id=\"21224\" type=\"exercise\" subtype=\"2\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"
      + "      </element>\r\n"
      + "      <element id=\"20676\" type=\"pres\" subtype=\"1000\" name=\"\" disabled=\"true\" keywords=\"\" partOfSpeech=\"\" frequency=\"2\">\r\n"
      + "        <element id=\"20677\" type=\"exercise\" subtype=\"1\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"2\" />\r\n"
      + "        <element id=\"20678\" type=\"exercise\" subtype=\"2\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"2\" />\r\n"
      + "      </element>\r\n"
      + "      <element id=\"18422\" type=\"pres\" subtype=\"1000\" name=\"Mot 3\" disabled=\"true\" keywords=\"\" partOfSpeech=\"\" frequency=\"3\">\r\n"
      + "        <element id=\"18423\" type=\"exercise\" subtype=\"1\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"3\" />\r\n"
      + "        <element id=\"18424\" type=\"exercise\" subtype=\"2\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"3\" />\r\n"
      + "      </element>\r\n" + "   </element>\r\n" + "   </element>\r\n" + "</course>";
  private final String expressions = "//element/element/element/element[@subtype=\"2\"]/@id";

  @Rule
  public final ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldReturnCorrectValueForGivenExpression() {
    assertThat(xmlParser.getValue(xmlFile.getBytes(), "//item/question/text()"))
        .isEqualTo("Question");
  }

  @Test
  public void shouldReturnArrayListWithThreeElements() {
    assertThat(xmlParser.getValues(courseXml.getBytes(), expressions).size()).isEqualTo(3);
  }

  @Test
  public void shouldReturn20678() {
    assertThat(xmlParser.getValues(courseXml.getBytes(), expressions)).element(1)
        .isEqualTo("20678");
  }

  @Test
  public void shouldReturnEmptyString() {
    assertThat(xmlParser.getValue(xmlFile.getBytes(), "invalid_expression")).isEmpty();
  }

  @Test
  public void shouldReturnEmptyArrayList() {
    assertThat(xmlParser.getValues(xmlFile.getBytes(), "invalid_expression")).isEmpty();
  }
}
