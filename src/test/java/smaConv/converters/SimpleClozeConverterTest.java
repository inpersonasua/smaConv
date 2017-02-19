package smaConv.converters;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import smaConv.util.AnkiCard;
import smaConv.util.Deck;
import smaConv.util.Parser;
import smaConv.util.SmpakParser;

public class SimpleClozeConverterTest {
  SimpleClozeConverter converter;
  Parser smpakParser;

  public static String courseXml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\r\n"//
      + "<course xmlns=\"http://www.supermemo.net/2006/smux\">\r\n" + "  <title>Title</title>\r\n"//
      + "  <element>\r\n" //
      + "    <element>\r\n" //
      + "      <element>\r\n"//
      + "        <element id=\"12345\" type=\"exercise\" subtype=\"2\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"//
      + "        <element id=\"12346\" type=\"exercise\" subtype=\"3\" name=\"\" keywords=\"\" partOfSpeech=\"\" frequency=\"1\" />\r\n"//
      + "      </element>\r\n" //
      + "    </element>\r\n" //
      + "   </element>\r\n" //
      + "</course>";

  public static String xmlFileName = "item12346.xml";
  public static String xmlFile = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + //
      "<item xmlns=\"http://www.supermemo.net/2006/smux\">\n" + //
      "  <question>\n" + //
      "<text><sentence></sentence><translation lang=\"pl\"><big>więcej</big><br /><br /></translation></text>\n"
      + //
      "<br/>\n" + //
      "After all is said and done, <droplist>\n" + //
      "<option correct=\"true\">more</option>\n" + //
      "<option>less</option>\n" + //
      "<option>nothing</option>\n" + //
      "</droplist> is said than done.</question>\n" + //
      "  <answer><text autoshow=\"true\"><sentence></sentence><translation lang=\"pl\">Gdy wszyskto zostało powiedziane i zrobione, więcej było powiedziane niż zrobiono.<br /><br /></translation></text>\n"
      + //
      "<br/>\n" + //
      "</answer>\n" + //
      "</item>";//

  public static String expectedQuestion = "After all is said and done, {{c1::more::więcej}} is said than done.";
  public static String expectedAnswer = "Gdy wszyskto zostało powiedziane i zrobione, więcej było powiedziane niż zrobiono.";

  @Before
  public void setUp() {
    smpakParser = mock(SmpakParser.class);
    when(smpakParser.getFile("course.xml")).thenReturn(courseXml.getBytes());
    when(smpakParser.getFile(xmlFileName)).thenReturn(xmlFile.getBytes());

    converter = new SimpleClozeConverter();
    converter.convertFile(xmlFile.getBytes(), xmlFileName);
  }

  @Test
  public void checkTemplates() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.getQuestionTemplate()).isEqualTo("{{cloze:sentence}}");
    assertThat(deck.getAnswerTemplate()).isEqualTo("{{cloze:sentence}}<br>{{translation}}");
  }

  @Test
  public void checkCardFields() {
    Deck<AnkiCard> deck = converter.makeDeck(smpakParser);
    assertThat(deck.get(0).getQuestion().get("sentence")).isEqualTo(expectedQuestion);
    assertThat(deck.get(0).getAnswer().get("translation")).isEqualTo(expectedAnswer);
  }
}
